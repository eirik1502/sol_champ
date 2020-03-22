package sol_game.game

import sol_engine.ecs.World
import sol_engine.module.ModulesHandler
import sol_engine.network.network_input.NetInputPacket
import sol_game.core_game.NetGameState
import sol_game.core_game.SolInputPacket

interface SolClientPlayer {

    fun onSetup()

    fun onStart(
            world: World,
            teamIndex: Int,
            playerIndex: Int
    )

    fun onUpdate(
            world: World,
            netGameState: NetGameState
    ): SolInputPacket

    fun onEnd(
            world: World,
            won: Boolean,
            winnerTeamIndex: Int,
            winnerPlayerIndex: Int
    )

}