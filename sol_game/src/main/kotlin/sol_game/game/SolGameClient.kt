package sol_game.game

import sol_engine.engine_interface.ThreadedSimulationLoop
import sol_engine.network.network_game.game_server.ServerConnectionData
import sol_engine.network.network_sol_module.NetworkServerModule
import sol_game.core_game.SolGameSimulationClient
import sol_game.core_game.SolGameSimulationServer
import kotlin.reflect.KClass


class SolGameClient(
        connectAddress: String,
        connectPort: Int,
        gameId: String,
        connectionKey: String,
        isObserver: Boolean,
        player: SolPlayer? = null,
        headless: Boolean = false,
        debugUI: Boolean = false  // cannot be set in headless mode
) {

    private var threadedLoop: ThreadedSimulationLoop? = null

    private val clientSim: SolGameSimulationClient = SolGameSimulationClient(
            connectAddress,
            connectPort,
            gameId,
            connectionKey,
            isObserver,
            player,
            headless,
            debugUI
    )

    fun setup() {
        clientSim.setup();
    }

    fun start() {
        threadedLoop = ThreadedSimulationLoop(clientSim)
        threadedLoop?.start()
    }

    fun waitUntilFinished() {
        threadedLoop?.waitUntilFinished()
    }

    fun terminate() {
        threadedLoop?.terminate()
    }
}