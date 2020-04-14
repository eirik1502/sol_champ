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
import sol_engine.physics_module.CollisionComp
import sol_engine.physics_module.PhysicsBodyComp
import sol_engine.physics_module.PhysicsBodyShape
import sol_engine.utils.reflection_utils.ClassUtils
import sol_game.core_game.SolActionsPacket
import sol_game.core_game.SolGameStateUtils
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
        ClassUtils.instantiateNoargs(config.playerClass)
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
            simulationShouldTerminate()
        } else if (gameStarted) {
            val gameState = SolGameStateUtils.retrieveSolGameState(world)
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