package sol_game.game

import sol_engine.ecs.World
import sol_engine.utils.math.MathF
import sol_game.core_game.SolActionsPacket

class SolRandomTestPlayer : SolClientPlayer {

    private var moveDirection: Int = 0

    override fun onSetup() {

    }

    override fun onStart(world: World, gameState: SolGameState) {

    }

    override fun onUpdate(world: World, gameState: SolGameState): SolActionsPacket {
        if (MathF.random() < 0.01f) {
            moveDirection = MathF.floori(MathF.random() * 5)
        }

        return SolActionsPacket(
                mvLeft = moveDirection == 0,
                mvRight = moveDirection == 1,
                mvUp = moveDirection == 2,
                mvDown = moveDirection == 3
        )
    }

    override fun onEnd(world: World, won: Boolean, winnerTeamIndex: Int, winnerPlayerIndex: Int) {

    }
}