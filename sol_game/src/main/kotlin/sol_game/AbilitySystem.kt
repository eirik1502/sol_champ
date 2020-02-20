package sol_game.core_game

import org.joml.Vector2f
import sol_engine.core.TransformComp
import sol_engine.ecs.Entity
import sol_engine.ecs.SystemBase
import sol_engine.input_module.InputComp
import sol_engine.physics_module.PhysicsBodyComp

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
            instanciateAbilityEntity(ability, entity, transformComp.position, inputComp.vectorInput("aimXY"))
        }
        if (abilityComp.executingAbilityExecutionTimer-- == 0) {
            resetExecutingAbility(abilityComp)
            ability.currentCooldown = ability.cooldown
        }
    }

    private fun instanciateAbilityEntity(ability: Ability, owner: Entity, characterPos: Vector2f, targetPos: Vector2f) {
        val cursorDir: Vector2f = targetPos.sub(characterPos, Vector2f())
//        val position = characterPos.add(offset, Vector2f())
        val impulse: Vector2f = cursorDir.mul(ability.initialImpulse, Vector2f())

        val abEntity = world.instanciateEntityClass(ability.abilityEntityClass, ability.abilityEntityClass)
                .modifyIfHasComponent(HitboxComp::class.java) { comp -> comp.owner = owner }
                .modifyIfHasComponent(PhysicsBodyComp::class.java) { comp -> comp.impulse.add(impulse) }
                .modifyIfHasComponent(SceneChildComp::class.java) { comp -> comp.parent = owner }
        val initialPosition =
                if (abEntity.hasComponent(SceneChildComp::class.java))
                // local offset position
                    Vector2f(ability.initialOffset, 0f)
                else
                // global offset position
                    characterPos.add(
                            cursorDir.normalize(Vector2f()).mul(ability.initialOffset),
                            Vector2f()
                    )
        abEntity.modifyIfHasComponent(TransformComp::class.java) { comp -> comp.setPosition(initialPosition) }
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
