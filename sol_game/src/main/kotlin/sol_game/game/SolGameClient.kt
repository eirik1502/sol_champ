package sol_game.game

import sol_engine.engine_interface.ThreadedSimulationLoop
import sol_game.core_game.SolGameSimulationClient


class SolGameClient(
        connectAddress: String,
        connectPort: Int,
        gameId: String,
        connectionKey: String,
        isObserver: Boolean,
        player: Class<out SolClientPlayer>? = null,
        headless: Boolean = false,
        debugUI: Boolean = false,  // cannot be set in headless mode
        allowGui: Boolean = true
) {

    private val clientSim: SolGameSimulationClient = SolGameSimulationClient(
            connectAddress,
            connectPort,
            gameId,
            connectionKey,
            isObserver,
            player,
            headless,
            debugUI,
            allowGui
    )

    private val threadedLoop: ThreadedSimulationLoop = ThreadedSimulationLoop(clientSim, 0f)

    fun setup() {
        threadedLoop.setup();
    }

    fun start() {
        threadedLoop.start()
    }

    fun waitUntilFinished() {
        threadedLoop.waitUntilFinished()
    }

    fun terminate() {
        threadedLoop.terminate()
    }
}