package sol_game.game

import sol_engine.ecs.World
import sol_engine.module.ModulesHandler
import sol_engine.network.network_input.NetInputPacket
import sol_game.core_game.NetGameState

interface SolPlayer {

    fun onStart(
            world: World,
            modules: ModulesHandler,
            teamIndex: Int,
            playerIndex: Int
    )

    fun onUpdate(
            world: World,
            modules: ModulesHandler,
            netGameState: NetGameState
    ): NetInputPacket

    fun onEnd(
            world: World,
            modules: ModulesHandler,
            winnerTeamIndex: Int
    )

}