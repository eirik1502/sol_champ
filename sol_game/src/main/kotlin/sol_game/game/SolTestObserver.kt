package sol_game.game

import sol_engine.ecs.World
import sol_game.core_game.SolActionsPacket

class SolTestObserver : SolClientPlayer {
    override fun onSetup() {

    }

    override fun onStart(world: World, gameState: SolGameState) {
    }

    override fun onUpdate(world: World, gameState: SolGameState): SolActionsPacket {
        println("observer updated")

        return SolActionsPacket()
    }

    override fun onEnd(world: World, won: Boolean, winnerTeamIndex: Int, winnerPlayerIndex: Int) {
    }
}