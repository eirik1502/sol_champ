package sol_game.core_game.components

import sol_engine.ecs.Component

class SolGameComp(
        var gameState: GameState = GameState.BEFORE_START,
        var teamIndexWon: Int = -1
) : Component() {

    enum class GameState {
        BEFORE_START,
        RUNNING,
        ENDING,
        ENDED
    }
}