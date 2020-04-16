package sol_game.core_game

import org.joml.Vector2f
import sol_engine.core.TransformComp
import sol_engine.ecs.Component
import sol_engine.ecs.Entity
import sol_engine.ecs.World
import sol_engine.network.network_ecs.host_managing.TeamPlayerComp
import sol_engine.physics_module.CollisionComp
import sol_engine.physics_module.PhysicsBodyComp
import sol_engine.physics_module.PhysicsBodyShape
import sol_game.core_game.components.*
import sol_game.game.*

object SolGameStateUtils {

    private fun <T : Component> getComponentOrCompute(entity: Entity, compType: Class<T>, default: () -> T): T {
        return if (entity.hasComponent(compType)) {
            entity.getComponent(compType)
        } else {
            default.invoke()
        }
    }

    private fun toRectangleObstacle(entitiesWithCollTrans: List<Entity>): List<RectangleObstacle> =
            entitiesWithCollTrans
                    .filter { it.getComponent(CollisionComp::class.java).bodyShape is PhysicsBodyShape.Rect }
                    .map {
                        val transComp = it.getComponent(TransformComp::class.java)
                        val bodyShape = it.getComponent(CollisionComp::class.java).bodyShape as PhysicsBodyShape.Rect
                        RectangleObstacle(
                                position = Vector2f(transComp.position),
                                size = Vector2f(bodyShape.width, bodyShape.height)
                        )
                    }

    private fun toCircleObstacle(entitiesWithCollTrans: List<Entity>): List<CircleObstacle> =
            entitiesWithCollTrans
                    .filter { it.getComponent(CollisionComp::class.java).bodyShape is PhysicsBodyShape.Circ }
                    .map {
                        val transComp = it.getComponent(TransformComp::class.java)
                        val bodyShape = it.getComponent(CollisionComp::class.java).bodyShape as PhysicsBodyShape.Circ
                        CircleObstacle(
                                position = Vector2f(transComp.position),
                                radius = bodyShape.radius
                        )
                    }

    fun retrieveStaticGameState(world: World): SolStaticGameState {
        val holeEntities = world.insight.entities
                .filter { it.hasComponents(setOf(HoleComp::class.java, CollisionComp::class.java, TransformComp::class.java)) }
        val rectangleHoles = toRectangleObstacle(holeEntities)
        val circleHoles = toCircleObstacle(holeEntities)

        val wallEntities = world.insight.entities
                .filter { it.hasComponents(setOf(WallComp::class.java, CollisionComp::class.java, TransformComp::class.java)) }
        val rectangleWalls = toRectangleObstacle(wallEntities)
        val circleWalls = toCircleObstacle(wallEntities)

        return SolStaticGameState(
                rectangleWalls = rectangleWalls,
                circleWalls = circleWalls,
                rectangleHoles = rectangleHoles,
                circleHoles = circleHoles
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

        val gameComp = world.insight.entities
                .asSequence()
                .find { it.hasComponent(SolGameComp::class.java) }
                ?.getComponent(SolGameComp::class.java)

        var gameStarted = gameComp?.gameState == SolGameComp.GameState.RUNNING ?: false
        val gameEnded = gameComp?.gameState == SolGameComp.GameState.ENDED ?: false
        val playerIndexWon = gameComp?.teamIndexWon ?: -1

        return SolGameState(
                gameStarted = gameStarted,
                gameEnded = gameEnded,
                playerIndexWon = playerIndexWon,
                charactersState = characterStates
        )
    }
}