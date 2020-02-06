@file:JvmName("Main")

package sol_game

import sol_game.networked_sol_game.NetworkedSolGameConfig
import sol_game.networked_sol_game.SolGameNetworked

fun main(args: Array<String>) {
//    SimulationLoop(SolGame()).start()
    val solGame = SolGameNetworked();
    val gameInfo = solGame.networkSetup(NetworkedSolGameConfig(
            2,
            true
    ))
    println(gameInfo)
    solGame.start()
    println("program should exit")
}