package sol_game.core_game

import sol_engine.engine_interface.SolSimulation
import sol_game.game.SolPlayer
import sol_game.sol_players.SolRandomTestPlayer

class SolGameBotSimulation(
        val simulation: SolGameSimulationOffline,
        val players: Map<Int, SolPlayer> = mapOf(0 to SolRandomTestPlayer())
) : SolSimulation() {

    private var startCalled = false


    override fun onSetupModules() {
        simulation.modulesHandler = modulesHandler
        simulation.onSetupModules()
    }

    override fun onSetupWorld() {
        simulation.world = world
        simulation.onSetupWorld()
    }

    override fun onStart() {
        players.values.forEach { it.onSetup() }
    }

    override fun onStepEnd() {
        val gameState = simulation.retrieveGameState()
        if (gameState.gameStarted) {
            if (!startCalled) {
                val staticGameState = simulation.retrieveStaticGameState()
                players.forEach { (playerIndex, player) -> player.onStart(playerIndex, staticGameState, gameState, world) }
                startCalled = true
            }

            val playersInputs = players.map { (playerIndex, player) ->
                playerIndex to player.onUpdate(playerIndex, gameState, world)
            }

            playersInputs.forEach { (playerIndex, playerInputs) -> simulation.setInputs(playerIndex, playerInputs) }

        }

    }

    override fun onEnd() {
        val gameState = simulation.retrieveGameState()
        players.forEach { (playerIndex, player) -> player.onEnd(playerIndex, gameState, world) }
    }
}