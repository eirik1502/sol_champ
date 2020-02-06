package sol_game.networked_sol_game

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

class SolGameNetworked {

    lateinit var server: Server
    lateinit var game: SolGameExternalIO
    lateinit var gameLoop: SimulationLoop

    fun networkSetup(config: NetworkedSolGameConfig): NetworkedSolGameInfo {
        val gameServerPort = findFreeNetPort()
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

    fun start() {
        server.start()
        if (server.waitForPlayerConnections()) {
            game = SolGameExternalIO(
                    server::pollPlayersInput,
                    server::pushGameState
            )
            gameLoop = SimulationLoop(game)
            gameLoop.start() // blocking until game ends
        } else {
            println("ERROR, server timed out without getting player connections")
        }

        server.stop()
    }
}