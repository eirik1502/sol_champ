@file:JvmName("Main")

package sol_game

import com.fasterxml.jackson.module.kotlin.*
import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default
import com.xenomachina.argparser.mainBody
import sol_engine.network.network_game.game_server.ServerConnectionData
import sol_game.core_game.AbilityConfig
import sol_game.core_game.AbilityType
import sol_game.core_game.CharacterConfig
import sol_game.game.*

data class PoolServerParams(
        val headless: Boolean
)

class Args(parser: ArgParser) {
    //    val poolServer by parser.option<PoolServerParams>(
//            "-p", "--poolServer",
//            argNames = listOf("HEADLESS"),
//            help = "run a simple pool server"
//    ) {
//        PoolServerParams(
//                arguments.getOrElse(0) { "true" }.toBoolean())
//    }
    val poolServer by parser.flagging(
            "-P", "--poolServer",
            help = "run a simple pool server")

    val headlessServer by parser.flagging(
            "-S", "--headlessServer",
            help = "run server with graphics. Default is headless")

    val client by parser.flagging(
            "-c", "--client",
            help = "run a client connecting to a pool server")

    val clientTeam by parser.storing(
            "-t", "--clientTeam",
            help = "What team the client should connect as (0 or 1)"
    ).default(0)

    val headlessClient by parser.flagging(
            "-s", "--headlessClient",
            help = "run server with graphics. Default is headless")
}

fun main(args: Array<String>) = mainBody {

    ArgParser(args).parseInto(::Args).run {
        println(this.poolServer)
        if (this.poolServer) {
            runPoolServer(headless = this.headlessServer)
        }

        if (this.client) {
            runConnectClientToPool(headless = this.headlessClient)
        }
    }
//    if (args.contains("poolServer")) {
//        runPoolServer()
//    } else if (args.contains("server")) {
//        runServer()
//    } else if (args.contains("client")) {
//        runConnectClientToPool()
//    }
}

val jsonMapper = jacksonObjectMapper()
val frankConfig: CharacterConfig = CharacterJsonDeserializer.fromFile("exampleFrankConfig.json")

val charactersConfig = listOf(
        frankConfig,
        frankConfig
)

fun runPoolServer(headless: Boolean) {
    val poolServer = SimpleSolGameServerPoolServer(headless = headless)
    poolServer.serve(55555, charactersConfig)
}

fun runConnectClientToPool(headless: Boolean) {
    val serverConnectionData = requestGameServerInstance(
            "localhost", 55555)
    if (serverConnectionData != null) {
        runClient(serverConnectionData, headless)
    } else {
        println("Could not connect to server")
    }
}

fun runServerClient() {
    val serverPool = SolGameServerPool(true)

    val serverConnectionData = serverPool.createServer(listOf(CharacterConfig(), CharacterConfig()))

    val client = runClient(serverConnectionData, true)

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

fun runClient(serverConnectionData: ServerConnectionData, headless: Boolean): SolGameClient {
    val client = SolGameClient(
            serverConnectionData.address,
            serverConnectionData.port,
            serverConnectionData.gameId,
            serverConnectionData.teamsPlayersKeys[0][0],
            false,
            headless = headless,
            debugUI = !headless,
            allowGui = !headless
    )

    client.setup()
    client.start()
    return client
}