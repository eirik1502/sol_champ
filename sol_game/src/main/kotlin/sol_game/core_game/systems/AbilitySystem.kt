package sol_game.core_game.systems

import org.joml.Vector2f
import sol_engine.core.TransformComp
import sol_engine.ecs.Entity
import sol_engine.ecs.SystemBase
import sol_engine.input_module.InputComp
import sol_engine.network.network_ecs.host_managing.TeamPlayerComp
import sol_engine.physics_module.PhysicsBodyComp
import sol_engine.utils.math.MathF
import sol_game.core_game.Ability
import sol_game.core_game.components.AbilityComp
import sol_game.core_game.components.HitboxComp
import sol_game.core_game.components.SceneChildComp

class AbilitySystem : SystemBase() {
    override fun onSetup() {
        usingComponents(AbilityComp::class.java, TransformComp::class.java, InputComp::class.java)
    }

    override fun onUpdate() {
        forEachWithComponents(
                AbilityComp::class.java,
                TransformComp::class.java,
                InputComp::class.java
        )
        { entity, abComp, transComp, userInputComp ->
            if (abComp.isExecuting) {
                handleExecutingAbility(entity, abComp, transComp, userInputComp)
            } else {
                abComp.abilities.stream()
                        .filter(Ability::trigger)
                        .filter() { ab -> ab.currentCooldown <= 0 }
                        .findFirst()
                        .ifPresent() { ab -> executeAbility(ab, abComp) }
            }
            // remove all triggers and update cooldowns
            abComp.abilities.stream()
                    .peek() { ab -> ab.trigger = false }
                    .filter() { ab -> ab.currentCooldown > 0 }
                    .forEach() { ab -> ab.currentCooldown-- }
        }
    }

    private fun handleExecutingAbility(entity: Entity, abilityComp: AbilityComp, transformComp: TransformComp, inputComp: InputComp) {
        val ability = abilityComp.executingAbility!!
        if (abilityComp.executingAbilityStartupDelayTimer-- == 0) {
            instanciateAbilityEntity(ability, entity, transformComp.position,
                    Vector2f(inputComp.floatInput("aimX"), inputComp.floatInput("aimY"))
            )
        }
        if (abilityComp.executingAbilityExecutionTimer-- == 0) {
            resetExecutingAbility(abilityComp)
            ability.currentCooldown = ability.cooldown
        }
    }

    private fun instanciateAbilityEntity(ability: Ability, owner: Entity, characterPos: Vector2f, targetPos: Vector2f) {
        val aimDirVec: Vector2f = targetPos.sub(characterPos, Vector2f())
        val normAimDirVec = aimDirVec.normalize(Vector2f())
//        val position = characterPos.add(offset, Vector2f())
        val impulse: Vector2f = normAimDirVec.mul(ability.initialImpulse, Vector2f())

        val abEntity = world.addEntity(ability.abilityEntityClass, ability.abilityEntityClass)
                .modifyIfHasComponent(HitboxComp::class.java) { comp -> comp.owner = owner }
                .modifyIfHasComponent(PhysicsBodyComp::class.java) { comp -> comp.velocity.add(impulse) }

        owner.modifyIfHasComponent(TeamPlayerComp::class.java) { ownerTeamPlayerComp ->
            abEntity.addComponentIfAbsent(TeamPlayerComp::class.java,
                    { TeamPlayerComp() },
                    { comp -> comp.copy(ownerTeamPlayerComp) }
            )
        }

        if (abEntity.hasComponent(SceneChildComp::class.java)) {
            // position relative to parent
            val initialRelativePos = Vector2f(ability.initialOffset, 0f)
            abEntity
                    .modifyIfHasComponent(TransformComp::class.java) { comp -> comp.setPosition(initialRelativePos) }
                    .modifyComponent(SceneChildComp::class.java) { comp -> comp.parent = owner }
        } else {
            val initialPosition = normAimDirVec.mul(ability.initialOffset, Vector2f()).add(characterPos)
            val pointDirection: Float = MathF.pointDirection(Vector2f(), normAimDirVec)
            abEntity.modifyIfHasComponent(TransformComp::class.java) { comp ->
                comp.setPosition(initialPosition).setRotationZ(pointDirection)
            }
        }
    }

    private fun resetExecutingAbility(abilityComp: AbilityComp) {
        abilityComp.executingAbility = null
        abilityComp.isExecuting = false
        abilityComp.executingAbilityExecutionTimer = -1
        abilityComp.executingAbilityStartupDelayTimer = -1
    }

    // does no checks to the AbilityComp state
    private fun executeAbility(ability: Ability, abilityComp: AbilityComp) {
        abilityComp.executingAbility = ability
        abilityComp.isExecuting = true
        abilityComp.executingAbilityExecutionTimer = ability.executeTime
        abilityComp.executingAbilityStartupDelayTimer = ability.startupDelay
    }
}
