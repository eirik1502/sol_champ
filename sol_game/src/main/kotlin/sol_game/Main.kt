@file:JvmName("Main")

package sol_game

import sol_engine.engine_interface.SimulationLoop
import sol_engine.network.network_game.game_server.ServerConnectionData
import sol_game.core_game.CharacterConfig
import sol_game.core_game.SolGameSimulationServer
import sol_game.game.SolGameServer

fun main(args: Array<String>) {

    val server = SolGameServer(
            charactersConfigs = listOf(CharacterConfig(), CharacterConfig())
    )
    val serverConnectionData: ServerConnectionData = server.setup()
    println("Server connection: $serverConnectionData")
    server.start()
    println("server started")
    server.terminate()
    println("Terminated")

//    val solServer = SolGameServer()
//    val gameConfig = solServer.networkSetup(SolGameServerConfig(
//            2, false
//    ))
//
//    val serverThread = Thread {
//        solServer.start()
//    }
//    serverThread.start()
//
//    Thread.sleep(1000)
//
//    val client1Loop = ThreadedSimulationLoop(SolGameGuiClient(
//            gameConfig.gameServerAddress,
//            gameConfig.gameServerPort,
//            gameConfig.gameId,
//            gameConfig.playersKeys[0],
//            0
//    ))
//
//    val client2Loop = ThreadedSimulationLoop(SolGameGuiClient(
//            gameConfig.gameServerAddress,
//            gameConfig.gameServerPort,
//            gameConfig.gameId,
//            gameConfig.playersKeys[1],
//            1,
//            headless = true
//    ))
//
//    client1Loop.start()
//    client2Loop.start()
//
//    serverThread.join()
//    client1Loop.join()
//    client2Loop.join()
}

//        SimulationLoop(SolGame()).start()

//    val solGame = SolGameNetworked();
//    val gameInfo = solGame.networkSetup(NetworkedSolGameConfig(
//            2,
//            true
//    ))
//    println(gameInfo)
//    solGame.start()
//    println("program should exit")
