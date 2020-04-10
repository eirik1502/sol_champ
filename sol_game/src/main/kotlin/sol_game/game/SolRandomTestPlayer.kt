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
        val myChar = gameState.charactersState[gameState.controlledPlayerIndex]
        val otherChar = gameState.charactersState.getOrNull((gameState.controlledPlayerIndex + 1) % 2)

        val aimX: Float = otherChar?.position?.x ?: MathF.randRange(0f, 1600f)
        val aimY: Float = otherChar?.position?.y ?: MathF.randRange(0f, 900f)

        if (MathF.random() < 0.1f) {
            moveDirection = MathF.randInt(0, 4)
        }

        val useAb1 = MathF.randInt(0, 60) == 0
        val useAb2 = MathF.randInt(0, 60 * 3) == 0
        val useAb3 = MathF.randInt(0, 60 * 5) == 0

        return SolActionsPacket(
                mvLeft = moveDirection == 0,
                mvRight = moveDirection == 1,
                mvUp = moveDirection == 2,
                mvDown = moveDirection == 3,
                ability1 = useAb1,
                ability2 = useAb2,
                ability3 = useAb3,
                aimX = aimX,
                aimY = aimY
        )
    }

    override fun onEnd(world: World, won: Boolean, winnerTeamIndex: Int, winnerPlayerIndex: Int) {

    }
}