package sol_game.core_game

import sol_engine.core.TransformComp
import sol_engine.ecs.Component
import sol_engine.ecs.Entity
import sol_engine.ecs.World
import sol_engine.network.network_ecs.host_managing.ClientControlledComp
import sol_engine.network.network_ecs.host_managing.TeamPlayerComp
import sol_engine.physics_module.CollisionComp
import sol_engine.physics_module.PhysicsBodyComp
import sol_engine.physics_module.PhysicsBodyShape
import sol_game.core_game.components.CharacterComp
import sol_game.core_game.components.HitboxComp
import sol_game.core_game.components.HurtboxComp
import sol_game.game.SolCharacterState
import sol_game.game.SolCharacterStateTag
import sol_game.game.SolGameState
import sol_game.game.SolHitboxState

object SolGameStateUtils {

    private fun <T : Component> getComponentOrCompute(entity: Entity, compType: Class<T>, default: () -> T): T {
        return if (entity.hasComponent(compType)) {
            entity.getComponent(compType)
        } else {
            default.invoke()
        }
    }

    fun retrieveSolGameState(world: World): SolGameState {
        val entitiesByTeam: List<List<Entity>> = world.insight.entities
                .filter { it.hasComponent(TeamPlayerComp::class.java) }
                .groupBy { it.getComponent(TeamPlayerComp::class.java).teamIndex }
                .toSortedMap(Comparator { teamIndex1, teamIndex2 -> teamIndex2 - teamIndex1 })
                .values.toList()

        val characterStates = entitiesByTeam.map { entitiesOfTeam ->
            val hitboxEntities = entitiesOfTeam.filter { entity -> entity.hasComponent(HitboxComp::class.java) }

            val hitboxStates = hitboxEntities.map { hitboxEntity ->
                val transComp = getComponentOrCompute(hitboxEntity, TransformComp::class.java) { TransformComp() }
                val physBodyComp = getComponentOrCompute(hitboxEntity, PhysicsBodyComp::class.java) { PhysicsBodyComp() }
                val collisionComp = getComponentOrCompute(hitboxEntity, CollisionComp::class.java) { CollisionComp() }
                val hitboxComp = getComponentOrCompute(hitboxEntity, HitboxComp::class.java) { HitboxComp() }
                SolHitboxState(
                        transComp.position,
                        physBodyComp.velocity,
                        (collisionComp.bodyShape as? PhysicsBodyShape.Circ)?.radius ?: run { -1f },
                        hitboxComp.damage,
                        hitboxComp.baseKnockback,
                        hitboxComp.knockbackRatio,
                        -1f,  // not implemented yet
                        false

                )
            }

            val characterEntity = entitiesOfTeam.find { entity -> entity.hasComponent(CharacterComp::class.java) }
            val characterState = characterEntity
                    ?.let { characterEntity ->
                        val transComp = getComponentOrCompute(characterEntity, TransformComp::class.java) { TransformComp() }
                        val physBodyComp = getComponentOrCompute(characterEntity, PhysicsBodyComp::class.java) { PhysicsBodyComp() }
                        val collisionComp = getComponentOrCompute(characterEntity, CollisionComp::class.java) { CollisionComp() }
                        val hurtboxComp = getComponentOrCompute(characterEntity, HurtboxComp::class.java) { HurtboxComp() }

                        SolCharacterState(
                                playerControlled = characterEntity.hasComponent(ClientControlledComp::class.java),
                                position = transComp.position,
                                velocity = physBodyComp.velocity,
                                acceleration = physBodyComp.acceleration,
                                rotation = transComp.rotationZ,
                                damage = hurtboxComp.totalDamageTaken,
                                radius = (collisionComp.bodyShape as? PhysicsBodyShape.Circ)?.radius ?: run { -1f },
                                stateTag = SolCharacterStateTag.CONTROLLED,
                                currentHitboxes = hitboxStates
                        )
                    }
                    ?: run { SolCharacterState(currentHitboxes = hitboxStates) }

            characterState
        }

        val controllingPlayerIndex = characterStates.indexOfFirst { it.playerControlled }
        return SolGameState(
                controlledPlayerIndex = controllingPlayerIndex,
                charactersState = characterStates
        )
    }
}