package sol_game.core_game.modules

import mu.KotlinLogging
import org.joml.Vector2f
import sol_engine.core.TransformComp
import sol_engine.ecs.Component
import sol_engine.ecs.Entity
import sol_engine.ecs.World
import sol_engine.input_module.InputSourceModule
import sol_engine.network.network_ecs.host_managing.ClientControlledComp
import sol_engine.network.network_ecs.host_managing.TeamPlayerComp
import sol_engine.network.network_sol_module.NetworkClientModule
import sol_engine.physics_module.CollisionComp
import sol_engine.physics_module.PhysicsBodyComp
import sol_engine.physics_module.PhysicsBodyShape
import sol_engine.utils.reflection_utils.ClassUtils
import sol_game.core_game.SolActionsPacket
import sol_game.core_game.components.CharacterComp
import sol_game.core_game.components.HitboxComp
import sol_game.core_game.components.HurtboxComp
import sol_game.game.*
import kotlin.Comparator

data class SolClientPlayerModuleConfig(
        val playerClass: Class<out SolClientPlayer>
)

class SolClientPlayerModule(
        private val config: SolClientPlayerModuleConfig
) : InputSourceModule() {
    private val logger = KotlinLogging.logger { }

    private lateinit var player: SolClientPlayer

    // should be set before start
    lateinit var world: World

    // should be set by player system when game starts
    var gameStarted: Boolean = false
    private var calledPlayerStart = false

    var teamIndexWon: Int = -1

    private var currSolActions: SolActionsPacket = SolActionsPacket()


    override fun onSetup() {
        ClassUtils.instanciateNoarg(config.playerClass)
                ?.let { player = it }
                ?: run {
                    logger.error { "Could not instanciate player of class: ${config.playerClass}" }
                }
        player.onSetup()
    }

    override fun onStart() {
    }

    override fun onUpdate() {
        if (teamIndexWon != -1) {
            player.onEnd(world, false, teamIndexWon, 0)
        } else if (gameStarted) {
            val gameState = retrieveSolGameState(world)
            if (gameState.controlledPlayerIndex != -1) {
                if (!calledPlayerStart) {
                    player.onStart(world, gameState)
                    calledPlayerStart = true
                }

                currSolActions = player.onUpdate(world, gameState)
            }

        }
    }

    override fun onEnd() {

    }

    override fun vectorInput(label: String?): Vector2f {
        logger.error { "Does not support vector input" }
        return Vector2f()
    }

    private fun <T : Component> getComponentOrCompute(entity: Entity, compType: Class<T>, default: () -> T): T {
        return if (entity.hasComponent(compType)) {
            entity.getComponent(compType)
        } else {
            default.invoke()
        }
    }

    private fun retrieveSolGameState(world: World): SolGameState {
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

    override fun checkAction(label: String?): Boolean {
        return when (label) {
            "mvLeft" -> currSolActions.mvLeft
            "mvRight" -> currSolActions.mvRight
            "mvUp" -> currSolActions.mvUp
            "mvDown" -> currSolActions.mvDown
            "ability1" -> currSolActions.ability1
            "ability2" -> currSolActions.ability2
            "ability3" -> currSolActions.ability3
            else -> false
        }
    }

    override fun floatInput(label: String?): Float {
        return when (label) {
            "aimX" -> currSolActions.aimX
            "aimY" -> currSolActions.aimY
            else -> 0f
        }
    }
}