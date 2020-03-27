@file:JvmName("Main")

package sol_game

import sol_engine.network.network_game.game_server.ServerConnectionData
import sol_game.core_game.CharacterConfig
import sol_game.game.SolGameClient
import sol_game.game.SolGameServer
import sol_game.game.SolGameServerPool

fun main(args: Array<String>) {

    val serverPool = SolGameServerPool(true)

    val serverConnectionData = serverPool.createServer(listOf(CharacterConfig(), CharacterConfig()))

    val client = runClient(serverConnectionData)

    client.waitUntilFinished()
    serverPool.stopAll()
    println("Terminated")
}

fun runServer(): SolGameServer {
    val server = SolGameServer(
            charactersConfigs = listOf(CharacterConfig(), CharacterConfig()),
            debugUI = true,
            allowGui = false
    )
    val serverConnectionData: ServerConnectionData = server.setup()
    println("Server connection: $serverConnectionData")
    server.start()
    println("server started")
    return server
}

fun runClient(serverConnectionData: ServerConnectionData): SolGameClient {
    val client = SolGameClient(
            serverConnectionData.address,
            serverConnectionData.port,
            serverConnectionData.gameId,
            serverConnectionData.teamsPlayersKeys[0][0],
            false,
            headless = false,
            debugUI = true,
            allowGui = true
    )

    client.setup()
    client.start()
    return client
}