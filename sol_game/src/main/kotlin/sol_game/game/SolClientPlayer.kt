package sol_game.game

import sol_engine.ecs.World
import sol_game.core_game.SolActionsPacket

interface SolClientPlayer {

    fun onSetup()

    fun onStart(
            world: World,
            gameState: SolGameState
    )

    fun onUpdate(
            world: World,
            gameState: SolGameState
    ): SolActionsPacket

    fun onEnd(
            world: World,
            won: Boolean,
            winnerTeamIndex: Int,
            winnerPlayerIndex: Int
    )

}