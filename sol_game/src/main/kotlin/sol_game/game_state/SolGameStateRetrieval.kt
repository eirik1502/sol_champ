package sol_game.game_state

import org.joml.Vector2f
import sol_engine.core.TransformComp
import sol_engine.ecs.Component
import sol_engine.ecs.EntitiesUtils
import sol_engine.ecs.Entity
import sol_engine.ecs.World
import sol_engine.network.network_ecs.host_managing.TeamPlayerComp
import sol_engine.physics_module.CollisionComp
import sol_engine.physics_module.PhysicsBodyComp
import sol_engine.physics_module.PhysicsBodyShape
import sol_game.core_game.components.*

object SolGameStateRetrieval {

    fun retrieveStaticGameState(world: World): SolStaticGameState {
        val holeEntities = world.insight.entities
                .filter { it.hasComponents(setOf(HoleComp::class.java, CollisionComp::class.java, TransformComp::class.java)) }
        val rectangleHoles = toRectangleObject(holeEntities)
        val circleHoles = toCircleObstacle(holeEntities)

        val wallEntities = world.insight.entities
                .filter { it.hasComponents(setOf(WallComp::class.java, CollisionComp::class.java, TransformComp::class.java)) }
        val rectangleWalls = toRectangleObject(wallEntities)
        val circleWalls = toCircleObstacle(wallEntities)

        return SolStaticGameState(
                walls = rectangleWalls + circleWalls,
                holes = rectangleHoles + circleHoles
        )
    }

    fun retrieveSolGameState(world: World): SolGameState {
        val entitiesByTeam: List<List<Entity>> = world.insight.entities
                .asSequence()
                .filter { it.hasComponent(TeamPlayerComp::class.java) }
                .groupBy { it.getComponent(TeamPlayerComp::class.java).teamIndex }
                .toSortedMap(Comparator { teamIndex1, teamIndex2 -> teamIndex1 - teamIndex2 })
                .values.toList()

        val characterStates = entitiesByTeam.map { entitiesOfTeam ->

            val hitboxEntities = entitiesOfTeam
                    .filter { entity ->
                        entity.hasComponents(setOf(
                                HitboxComp::class.java,
                                TransformComp::class.java,
                                PhysicsBodyComp::class.java,
                                CollisionComp::class.java
                        ))
                    }
                    .filter { it.getComponent(CollisionComp::class.java).bodyShape is PhysicsBodyShape.Circ }

            val hitboxStates = hitboxEntities
                    .map { entity ->
                        val transComp = entity.getComponent(TransformComp::class.java)
                        val physicsBodyComp = entity.getComponent(PhysicsBodyComp::class.java)
                        val collisionComp = entity.getComponent(CollisionComp::class.java)
                        val hitboxComp = entity.getComponent(HitboxComp::class.java)

                        SolHitboxState(
                                CircleObjectState(
                                        Vector2f(transComp.position),
                                        (collisionComp.bodyShape as PhysicsBodyShape.Circ).radius
                                ),
                                Vector2f(physicsBodyComp.velocity),
                                hitboxComp.damage,
                                hitboxComp.baseKnockback,
                                hitboxComp.knockbackRatio,
                                -1f,  // not implemented yet
                                false
                        )
                    }


            val characterEntity = entitiesOfTeam.find { entity ->
                entity.hasComponents(setOf(
                        CharacterComp::class.java,
                        TransformComp::class.java,
                        PhysicsBodyComp::class.java,
                        CollisionComp::class.java,
                        HurtboxComp::class.java,
                        StockComp::class.java
                ))
            }
            val characterState = characterEntity
                    ?.let { characterEntity ->
                        val transComp = characterEntity.getComponent(TransformComp::class.java)
                        val physBodyComp = characterEntity.getComponent(PhysicsBodyComp::class.java)
                        val collisionComp = characterEntity.getComponent(CollisionComp::class.java)
                        val hurtboxComp = characterEntity.getComponent(HurtboxComp::class.java)
                        val stockComp = characterEntity.getComponent(StockComp::class.java)

                        SolCharacterState(
                                physicalObject = CircleObjectState(
                                        position = Vector2f(transComp.position),
                                        radius = (collisionComp.bodyShape as? PhysicsBodyShape.Circ)?.radius
                                                ?: run { -1f }
                                ),
                                velocity = Vector2f(physBodyComp.velocity),
                                acceleration = Vector2f(physBodyComp.acceleration),
                                rotation = transComp.rotationZ,
                                damage = hurtboxComp.totalDamageTaken,
                                stocks = stockComp.currentStockCount,
                                stateTag = SolCharacterStateTag.CONTROLLED,
                                currentHitboxes = hitboxStates
                        )
                    }
                    ?: run { SolCharacterState(currentHitboxes = hitboxStates) }

            characterState
        }

        val gameComp = world.insight.entities
                .asSequence()
                .find { it.hasComponent(SolGameComp::class.java) }
                ?.getComponent(SolGameComp::class.java)

        var gameStarted = gameComp?.let { it.gameState != SolGameComp.GameState.BEFORE_START } ?: false
        val gameEnded = gameComp?.let { it.gameState == SolGameComp.GameState.ENDED } ?: false
        val playerIndexWon = gameComp?.teamIndexWon ?: -1

        return SolGameState(
                gameStarted = gameStarted,
                gameEnded = gameEnded,
                
                playerIndexWon = playerIndexWon,
                charactersState = characterStates
        )
    }


    private fun toRectangleObject(entitiesWithCollTrans: List<Entity>): List<RectangleObjectState> =
            entitiesWithCollTrans
                    .filter { it.getComponent(CollisionComp::class.java).bodyShape is PhysicsBodyShape.Rect }
                    .map {
                        val transComp = it.getComponent(TransformComp::class.java)
                        val bodyShape = it.getComponent(CollisionComp::class.java).bodyShape as PhysicsBodyShape.Rect
                        RectangleObjectState(
                                position = Vector2f(transComp.position),
                                size = Vector2f(bodyShape.width, bodyShape.height)
                        )
                    }

    private fun toCircleObstacle(entitiesWithCollTrans: List<Entity>): List<CircleObjectState> =
            entitiesWithCollTrans
                    .filter { it.getComponent(CollisionComp::class.java).bodyShape is PhysicsBodyShape.Circ }
                    .map {
                        val transComp = it.getComponent(TransformComp::class.java)
                        val bodyShape = it.getComponent(CollisionComp::class.java).bodyShape as PhysicsBodyShape.Circ
                        CircleObjectState(
                                position = Vector2f(transComp.position),
                                radius = bodyShape.radius
                        )
                    }
}