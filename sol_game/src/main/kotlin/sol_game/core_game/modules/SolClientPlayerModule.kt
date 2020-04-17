package sol_game.core_game.modules

import mu.KotlinLogging
import sol_engine.ecs.World
import sol_engine.input_module.InputSourceModule
import sol_engine.network.network_ecs.host_managing.ClientControlledComp
import sol_engine.utils.reflection_utils.ClassUtils
import sol_game.core_game.SolActions
import sol_game.game_state.SolGameStateRetrieval
import sol_game.core_game.components.CharacterComp
import sol_game.game.*

data class SolClientPlayerModuleConfig(
        val playerClass: Class<out SolPlayer>
)

class SolClientPlayerModule(
        private val config: SolClientPlayerModuleConfig
) : InputSourceModule() {
    private val logger = KotlinLogging.logger { }

    private lateinit var player: SolPlayer

    // should be set before start
    lateinit var world: World

    // should be set by player system when game starts
    var gameStarted: Boolean = false
    private var calledPlayerStart = false

    var teamIndexWon: Int = -1

    private var currSolActions: SolActions = SolActions()


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

    // returns -1 if no controlled player found
    private fun retrieveControlledCharacterIndex(): Int =
            world.insight.entities
                    .indexOfFirst { it.hasComponents(setOf(CharacterComp::class.java, ClientControlledComp::class.java)) }


    override fun onUpdate() {
        val controlledPlayerIndex = retrieveControlledCharacterIndex()
        if (controlledPlayerIndex != -1) {
            val gameState = SolGameStateRetrieval.retrieveSolGameState(world)

            if (teamIndexWon != -1) {
                player.onEnd(controlledPlayerIndex, gameState, world)
                simulationShouldTerminate()
            } else if (gameStarted) {

                if (!calledPlayerStart) {
                    player.onStart(controlledPlayerIndex, SolGameStateRetrieval.retrieveStaticGameState(world), gameState, world)
                    calledPlayerStart = true
                }

                currSolActions = player.onUpdate(controlledPlayerIndex, gameState, world)
            }
        }
    }

    override fun onEnd() {

    }


    override fun checkTrigger(label: String?): Boolean {
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

    override fun hasTrigger(label: String?): Boolean =
            setOf("mvLeft", "mvRight", "mvUp", "mvDown", "ability1", "ability2", "ability3").contains(label)


    override fun hasFloatInput(label: String?): Boolean = setOf("aimX", "aimY").contains(label)
}