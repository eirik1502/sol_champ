package sol_game.game

import org.slf4j.LoggerFactory
import sol_engine.engine_interface.SimulationLoop
import sol_engine.engine_interface.ThreadedSimulationLoop
import sol_engine.network.network_game.game_server.GameServerConfig
import sol_engine.network.network_game.game_server.ServerConnectionData
import sol_engine.network.network_sol_module.NetworkServerModule
import sol_game.core_game.CharacterConfig
import sol_game.core_game.SolGameSimulationServer
import sol_game.networked_sol_game.Server

typealias TerminationCallback = (gameServer: SolGameServer) -> Unit

class SolGameServer(
        charactersConfigs: List<CharacterConfig> = listOf(),
        requestPort: Int = DEFAULT_PORT,
        allowObservers: Boolean = true,
        headless: Boolean = false,
        debugUI: Boolean = false,  // cannot be set in headless mode
        // gui is not supported when running multiple instances of SolGameServer / Client on multiple threads
        allowGui: Boolean = true
) {

    private val serverSim: SolGameSimulationServer = SolGameSimulationServer(
            charactersConfigs,
            requestPort,
            allowObservers,
            headless,
            debugUI,
            allowGui
    )

    private val threadedLoop: ThreadedSimulationLoop = ThreadedSimulationLoop(serverSim);

    fun onTermination(callback: TerminationCallback) {
        threadedLoop.onTermination() { threadedLoop, loop, sim -> callback(this) }
    }

    fun setup(): ServerConnectionData {
        threadedLoop.setup();
        return serverSim.modulesHandler.getModule(NetworkServerModule::class.java).connectionData
    }

    fun start() {
        threadedLoop.start()
    }

    fun getConnectionData(): ServerConnectionData {
        return serverSim.modulesHandler.getModule(NetworkServerModule::class.java).connectionData
    }

    fun waitUntilFinished() {
        threadedLoop.waitUntilFinished()
    }

    fun terminate() {
        threadedLoop.terminate()
    }
}