package sol_game.game

import sol_engine.network.network_game.game_server.ServerConnectionData
import sol_game.core_game.CharacterConfig

class SolGameServerPool(
        val headless: Boolean = true
) {

    private val runningServers: MutableMap<String, SolGameServer> = HashMap()


    fun createServer(
            charactersConfigs: List<CharacterConfig>,
            allowObservers: Boolean = true
    ): ServerConnectionData {
        val server = SolGameServer(
                charactersConfigs = charactersConfigs,
                requestPort = -1,  // let the server find a port
                allowObservers = allowObservers,
                headless = headless
        )
        val serverConnectData = server.setup()
        server.onTermination { runningServers.remove(serverConnectData.gameId) }
        server.start()
        runningServers[serverConnectData.gameId] = server
        return serverConnectData
    }

    fun stopAll() {
        runningServers.values.forEach() { it.terminate() }
    }
}