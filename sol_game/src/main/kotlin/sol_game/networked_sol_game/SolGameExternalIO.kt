package sol_game.core_game_wrappers

import sol_engine.core.TransformComp
import sol_engine.input_module.ExternalInputSourceModule
import sol_game.core_game.CharacterComp
import sol_game.core_game.SolGame

class SolGameExternalIO(
        val pollPlayersInput: () -> PlayersInput,
        val pushGameState: (gameState: GameState) -> Unit,
        private val headless: Boolean = false,
        private val debugMode: Boolean = false
) : SolGame(headless, false, debugMode = debugMode) {


    override fun onStepStart() {
        val inputSourceModule = modulesHandler.getModule(ExternalInputSourceModule::class.java);
        val playersInput = pollPlayersInput()

        playersInput.playersInput.forEachIndexed() { i, playerInput ->
            val groupPrefix = "player${i}:"
            inputSourceModule.updateTriggerInputs(mapOf(
                    "${groupPrefix}moveLeft" to playerInput.moveLeft,
                    "${groupPrefix}moveRight" to playerInput.moveRight,
                    "${groupPrefix}moveUp" to playerInput.moveUp,
                    "${groupPrefix}moveDown" to playerInput.moveDown,
                    "${groupPrefix}ability1" to playerInput.ability1,
                    "${groupPrefix}ability2" to playerInput.ability2,
                    "${groupPrefix}ability3" to playerInput.ability3
            ))
        }

        super.onStepStart()
    }

    override fun onStepEnd() {
        super.onStepEnd()

        val playersState = world.insight.entities
                .filter { e -> e.hasComponent(CharacterComp::class.java) }
                .map { e ->
                    val transformComp = e.getComponent(TransformComp::class.java)
                    PlayerStateOutput(transformComp.x, transformComp.y, transformComp.rotationZ)
                }
                .toList()
        val gameState = GameState(playersState)
        pushGameState(gameState)
    }

}