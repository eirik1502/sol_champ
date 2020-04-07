@file:JvmName("Main")

package sol_game

import sol_engine.network.network_game.game_server.ServerConnectionData
import sol_game.core_game.AbilityConfig
import sol_game.core_game.AbilityType
import sol_game.core_game.CharacterConfig
import sol_game.game.*

fun main(args: Array<String>) {
    if (args.contains("poolServer")) {
        runPoolServer()
    } else if (args.contains("server")) {
        runServer()
    } else if (args.contains("client")) {
        runConnectClientToPool()
    }
}

val charactersConfig = listOf(
        CharacterConfig(
                name = "Frank",
                radius = 64f,
                moveVelocity = 48f,
                abilities = listOf(
                        AbilityConfig(
                                name = "rapid shot",
                                type = AbilityType.MELEE,
                                radius = 48f,
                                distanceFromChar = 128f,
                                speed = 10f,

                                activeTime = 10,
                                startupTime = 5,
                                endlagTime = 3,
                                rechargeTime = 10,

                                damage = 20f,
                                baseKnockback = 100f,
                                knockbackRatio = 1f,
                                knockbackPoint = 32f,
                                knockbackTowardPoint = false
                        ),
                        AbilityConfig(name = "big blast"),
                        AbilityConfig(name = "get off")
                )
        ),
        CharacterConfig(
                name = "Schmatias",
                abilities = listOf(
                        AbilityConfig(name = "bam"),
                        AbilityConfig(name = "hook"),
                        AbilityConfig(name = "shmack")
                )
        )
)

fun runPoolServer() {
    val poolServer = SimpleSolGameServerPoolServer(headless = false)
    poolServer.serve(55555, charactersConfig)
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