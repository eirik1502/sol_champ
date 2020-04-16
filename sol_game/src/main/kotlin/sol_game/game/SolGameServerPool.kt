package sol_game.game

import sol_engine.network.network_game.game_server.ServerConnectionData
import sol_game.core_game.CharacterConfig
import java.util.concurrent.ConcurrentHashMap

class SolGameServerPool(
        val headless: Boolean = true,
        val disableGui: Boolean = false
) {

    private val runningServers: MutableMap<String, SolGameServer> = ConcurrentHashMap()


    fun createServer(
            charactersConfigs: List<CharacterConfig>,
            allowObservers: Boolean = true
    ): ServerConnectionData {
        val server = SolGameServer(
                charactersConfigs = charactersConfigs,
                requestPort = -1,  // let the server find a port
                allowObservers = allowObservers,
                updateFrameTime = 0f,
                headless = headless,
                debugUI = !headless,
                allowGui = !disableGui
        )
        val serverConnectData = server.setup()
        server.onTermination {
            runningServers.remove(serverConnectData.gameId)
        }
        server.start()
        runningServers[serverConnectData.gameId] = server
        return serverConnectData
    }

    fun getRunningServer(): List<String> {
        return runningServers.keys.toList()
    }

    fun getConnectionDataOfServer(gameId: String): ServerConnectionData? {
        return runningServers[gameId]?.getConnectionData()
    }

    fun stopAll() {
        runningServers.values.forEach() { it.terminate() }
    }
}