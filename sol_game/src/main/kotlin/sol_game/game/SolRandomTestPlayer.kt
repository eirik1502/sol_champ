package sol_game.game

import sol_engine.ecs.World
import sol_engine.utils.math.MathF
import sol_game.core_game.SolActions

class SolRandomTestPlayer : SolClientPlayer {

    private var moveDirection: Int = 0

    override fun onSetup() {

    }

    override fun onStart(controlledCharacterIndex: Int, gameState: SolGameState, world: World) {
    }

    override fun onUpdate(controlledCharacterIndex: Int, gameState: SolGameState, world: World): SolActions {
        val myChar = gameState.charactersState[controlledCharacterIndex]
        val otherChar = gameState.charactersState.getOrNull((controlledCharacterIndex + 1) % 2)

        val aimX: Float = otherChar?.position?.x ?: MathF.randRange(0f, 1600f)
        val aimY: Float = otherChar?.position?.y ?: MathF.randRange(0f, 900f)

        if (MathF.random() < 0.1f) {
            moveDirection = MathF.randInt(0, 4)
        }

        val useAb1 = MathF.randInt(0, 60) == 0
        val useAb2 = MathF.randInt(0, 60 * 3) == 0
        val useAb3 = MathF.randInt(0, 60 * 5) == 0

        return SolActions(
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

    override fun onEnd(controlledCharacterIndex: Int, gameState: SolGameState, world: World) {
    }

}