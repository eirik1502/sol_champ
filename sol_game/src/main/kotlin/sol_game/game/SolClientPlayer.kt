package sol_game.game

import sol_engine.ecs.World
import sol_game.core_game.SolActions

interface SolClientPlayer {

    fun onSetup()

    fun onStart(
            controlledCharacterIndex: Int,
            gameState: SolGameState,
            world: World
    )

    fun onUpdate(
            controlledCharacterIndex: Int,
            gameState: SolGameState,
            world: World
    ): SolActions

    fun onEnd(
            controlledCharacterIndex: Int,
            gameState: SolGameState,
            world: World
    )

}