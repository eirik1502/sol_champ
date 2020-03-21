package sol_game.networked_sol_game

import mu.KotlinLogging
import sol_engine.engine_interface.SimulationLoop
import sol_engine.utils.Repeat
import java.util.*


fun uuid(): String {
    return UUID.randomUUID().toString()
}

fun errorGameInfo(): NetworkedSolGameInfo {
    return NetworkedSolGameInfo(
            true,
            "-1",
            "-1",
            -1,
            listOf(),
            false,
            null
    )
}

private val logger = KotlinLogging.logger { }

class SolGameServer {

    lateinit var server: Server
    var game: SolGameExternalIO? = null
    lateinit var gameLoop: SimulationLoop

    fun networkSetup(config: SolGameServerConfig): NetworkedSolGameInfo {
        val gameServerPort = findFreeSocketPort()
        if (gameServerPort == -1) {
            return errorGameInfo()
        }
        val gameId = uuid()
        val playersKeys: List<String> = Repeat.listConstructor(config.playerCount) { _ -> uuid() }


        server = Server(gameServerPort, gameId, playersKeys)

        val gameServerAddress = "localhost"
        val observerKey = if (config.allowObservers) uuid() else null

        return NetworkedSolGameInfo(
                false,
                gameId,
                gameServerAddress,
                gameServerPort,
                playersKeys,
                config.allowObservers,
                observerKey
        )
    }

    fun start(headless: Boolean = true) {
        server.start()
        logger.info { "Sol server started, waiting for connections" }

        if (server.waitForPlayerConnections()) {
            logger.info { "all players connected, starting game" }
            game = SolGameExternalIO(
                    server::pollPlayersInput,
                    server::pushGameState,
                    headless = headless
            )
            gameLoop = SimulationLoop(game)
            gameLoop.start() // blocking until game ends
        } else {
            println("ERROR, server timed out without getting player connections")
        }

        stop()
    }

    fun stop() {
        game?.let { game ->
            if (!game.isTerminated) {
                game.terminate()
            }
        }
        server.stop()
    }
}