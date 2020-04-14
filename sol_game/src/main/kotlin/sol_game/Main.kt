@file:JvmName("Main")

package sol_game

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default
import com.xenomachina.argparser.mainBody
import sol_engine.network.network_game.game_server.ServerConnectionData
import sol_game.core_game.CharacterConfig
import sol_game.game.*


data class ServerConfig(
        val use: Boolean = false,
        val headless: Boolean = false
)

data class ClientConfig(
        val teamIndex: Int,
        val headless: Boolean
)

class Args(parser: ArgParser) {
    val poolServer by parser.storing(
            "--poolServer",
            help = "run a simple pool server. Followed by comma separated options: [headless?]"
    ) {
        val options = this.split(",")
        val headless = options.contains("headless")
        ServerConfig(true, headless)
    }.default(ServerConfig())

    val clients by parser.adding(
            "--client",
            help = "Clients to connect to the pool server. Followed by comma separated options: [teamIndex?=(int = 0),headless?]"
    ) {
        val args = this.split(",")
        val teamIndex = args.find { arg -> arg.startsWith("teamindex") }?.split("=")?.getOrNull(1)
                ?.toInt()
                ?: 0
        val headless = args.contains("headless")
        ClientConfig(teamIndex, headless)
    }

    val runExhaustion by parser.flagging(
            "--runExhaustion",
            help = "Run many sped up games with headless server and clients"
    )
}

fun main(args: Array<String>) = mainBody {
    ArgParser(args).parseInto(::Args).run {
        if (runExhaustion) {
            runManyFastSimulations()
        }
        if (this.poolServer.use) {
            println("Running pool server: $poolServer")
            runPoolServer(headless = this.poolServer.headless)
        }

        clients.forEach {
            println("Running client: $it")
            runConnectClientToPool(it.headless, it.teamIndex)
//            Thread.sleep(500)
        }
    }
}

val frankConfig: CharacterConfig = CharacterConfigLoader.fromResourceFile("exampleFrankConfig.json")

val charactersConfig = listOf(
        frankConfig,
        frankConfig
)

fun runPoolServer(headless: Boolean) {
    val poolServer = SimpleSolGameServerPoolServer(headless = headless)
    poolServer.serve(55555, charactersConfig)
}

fun runConnectClientToPool(headless: Boolean, teamIndex: Int) {
    val serverConnectionData = requestGameServerInstance(
            "localhost", 55555)
    if (serverConnectionData != null) {
        runClient(serverConnectionData, headless, teamIndex)
    } else {
        println("Could not connect to server")
    }
}

fun runServerClient() {
    val serverPool = SolGameServerPool(true)

    val serverConnectionData = serverPool.createServer(listOf(CharacterConfig(), CharacterConfig()))

    val client = runClient(serverConnectionData, true, 0)

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

fun runClient(serverConnectionData: ServerConnectionData, headless: Boolean, teamIndex: Int): SolGameClient {
    val client = SolGameClient(
            serverConnectionData.address,
            serverConnectionData.port,
            serverConnectionData.gameId,
            serverConnectionData.teamsPlayersKeys[teamIndex][0],
            false,
            player = SolRandomTestPlayer::class.java,
            updateFrameTime = 0f,
            headless = headless,
            debugUI = !headless,
            allowGui = !headless
    )

    client.setup()
    client.start()
    return client
}

fun runManyFastSimulations() {
    val teamWins = (0..300)
            .map {
                println("running simulation $it")
                runFastSimulation()
            }
    println("player 0 won: ${teamWins.filter { it == 0 }.count()}")
    println("player 1 won: ${teamWins.filter { it == 1 }.count()}")
    println("ties: ${teamWins.filter { it == -1 }.count()}")
}

fun runFastSimulation(): Int {
    val server = SolGameServer(
            charactersConfigs = listOf(frankConfig, frankConfig),
            updateFrameTime = 0f,
            headless = false
    )
    val serverConnectionData: ServerConnectionData = server.setup()
    println("Server connection: $serverConnectionData")
    server.start()
    println("server started")

    val clients = (0..1)
            .map { teamIndex ->
                SolGameClient(
                        serverConnectionData.address,
                        serverConnectionData.port,
                        serverConnectionData.gameId,
                        serverConnectionData.teamsPlayersKeys[teamIndex][0],
                        false,
                        player = SolRandomTestPlayer::class.java,
                        updateFrameTime = 0f,
                        headless = true
                )
            }
            .onEach { it.setup() }
            .onEach {
                Thread.sleep(100)
                it.start()
            }
//    server.waitUntilFinished()
    Thread.sleep(3000)
    server.terminate()
    server.waitUntilFinished()
    clients.forEach { it.terminate() }
    return server.getTeamIndexWon()
}