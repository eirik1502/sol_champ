package sol_game.game

import sol_engine.ecs.World
import sol_game.core_game.SolActions
import sol_game.game_state.SolGameState
import sol_game.game_state.SolStaticGameState

interface SolPlayer {

    fun onSetup()

    fun onStart(
            controlledCharacterIndex: Int,
            staticGameState: SolStaticGameState,
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