package sol_game.game

import sol_engine.ecs.World
import sol_game.core_game.SolGameStatePacket
import sol_game.core_game.SolActionsPacket

interface SolClientPlayer {

    fun onSetup()

    fun onStart(
            world: World,
            teamIndex: Int,
            playerIndex: Int
    )

    fun onUpdate(
            world: World,
            solGameStatePacket: SolGameStatePacket
    ): SolActionsPacket

    fun onEnd(
            world: World,
            won: Boolean,
            winnerTeamIndex: Int,
            winnerPlayerIndex: Int
    )

}