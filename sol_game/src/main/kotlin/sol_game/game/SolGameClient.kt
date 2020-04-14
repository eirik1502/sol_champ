package sol_game.game

import mu.KotlinLogging
import sol_engine.engine_interface.ThreadedSimulationLoop
import sol_game.core_game.SolGameSimulationClient


class SolGameClient(
        connectAddress: String,
        connectPort: Int,
        gameId: String,
        connectionKey: String,
        isObserver: Boolean,
        player: Class<out SolClientPlayer>? = null,
        updateFrameTime: Float = 1f / 60f,  // set to run the game at a custom fixed frame times
        headless: Boolean = false,
        debugUI: Boolean = false,  // cannot be set in headless mode
        allowGui: Boolean = true
) {
    private val logger = KotlinLogging.logger { }

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

    private val threadedLoop: ThreadedSimulationLoop = ThreadedSimulationLoop(clientSim, updateFrameTime)

    fun setup() {
        threadedLoop.setup();
        threadedLoop.onTermination { _, _, _ -> logger.info { "SolClient finished" } }
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