package sol_game.game

import org.slf4j.LoggerFactory
import sol_engine.engine_interface.SimulationLoop
import sol_engine.engine_interface.ThreadedSimulationLoop
import sol_engine.network.network_game.game_server.GameServerConfig
import sol_engine.network.network_game.game_server.ServerConnectionData
import sol_engine.network.network_sol_module.NetworkServerModule
import sol_game.core_game.CharacterConfig
import sol_game.core_game.SolGameSimulationServer

class SolGameServer(
        charactersConfigs: List<CharacterConfig> = listOf(),
        requestPort: Int = -1,
        allowObservers: Boolean = true,
        headless: Boolean = false,
        debugUI: Boolean = false  // cannot be set in headless mode
) {

    private var threadedLoop: ThreadedSimulationLoop? = null

    private val serverSim: SolGameSimulationServer = SolGameSimulationServer(
            charactersConfigs,
            requestPort,
            allowObservers,
            headless,
            debugUI
    )

    fun setup(): ServerConnectionData {
        serverSim.setup();
        return serverSim.modulesHandler.getModule(NetworkServerModule::class.java).connectionData
    }

    fun start() {
        threadedLoop = ThreadedSimulationLoop(serverSim)
        threadedLoop?.start()
    }

    fun waitUntilFinished() {
        threadedLoop?.waitUntilFinished()
    }

    fun terminate() {
        threadedLoop?.terminate()
    }
}