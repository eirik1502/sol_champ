package sol_game.game

import sol_engine.ecs.World
import sol_game.core_game.SolActions
import sol_game.game_state.SolGameState
import sol_game.game_state.SolStaticGameState

class SolTestObserver : SolPlayer {
    override fun onSetup() {

    }

    override fun onStart(controlledCharacterIndex: Int, staticGameState: SolStaticGameState, gameState: SolGameState, world: World) {

    }

    override fun onUpdate(controlledCharacterIndex: Int, gameState: SolGameState, world: World): SolActions {
        println("observer updated")

        return SolActions()
    }

    override fun onEnd(controlledCharacterIndex: Int, gameState: SolGameState, world: World) {
    }


}