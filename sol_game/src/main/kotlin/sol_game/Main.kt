@file:JvmName("Main")

package sol_game

import sol_engine.network.network_game.game_server.ServerConnectionData
import sol_game.core_game.CharacterConfig
import sol_game.game.SolGameClient
import sol_game.game.SolGameServer

fun main(args: Array<String>) {

    val server = SolGameServer(
            charactersConfigs = listOf(CharacterConfig(), CharacterConfig()),
            debugUI = true,
            allowGui = false
    )
    val serverConnectionData: ServerConnectionData = server.setup()
    println("Server connection: $serverConnectionData")
    server.start()
    println("server started")

    val client = SolGameClient(
            serverConnectionData.address,
            serverConnectionData.port,
            serverConnectionData.gameId,
            serverConnectionData.teamsPlayersKeys[0][0],
            false,
            headless = true,
            debugUI = true,
            allowGui = false
    )

    client.setup()
    client.start()

    server.waitUntilFinished();
    println("Terminated")
}
