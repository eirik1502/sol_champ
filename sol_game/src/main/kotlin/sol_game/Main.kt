@file:JvmName("Main")

package sol_game

import mu.toKLogger
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.impl.SimpleLogger
import org.slf4j.impl.SimpleLoggerConfiguration
import sol_engine.network.communication_layer.PacketClassStringConverter
import sol_engine.network.network_game.game_server.ServerConnectionData
import sol_game.core_game.CharacterConfig
import sol_game.game.*
import sol_game.networked_sol_game.SolGameServerConfig
import java.util.*

fun main(args: Array<String>) {
    if (args.contains("poolServer")) {
        runPoolServer()
    } else if (args.contains("server")) {
        runServer()
    } else if (args.contains("client")) {
        runConnectClientToPool()
    }
}

fun runPoolServer() {
    val poolServer = SimpleSolGameServerPoolServer(headless = false)
    poolServer.serve(55555, listOf(CharacterConfig(), CharacterConfig()))
}

fun runConnectClientToPool() {
    val serverConnectionData = requestGameServerInstance(
            "localhost", 55555)
    if (serverConnectionData != null) {
        runClient(serverConnectionData)
    } else {
        println("Could not connect to server")
    }
}

fun runServerClient() {
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