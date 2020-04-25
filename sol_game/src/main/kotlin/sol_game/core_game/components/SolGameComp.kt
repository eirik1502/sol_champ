package sol_game.core_game.components

import org.joml.Vector2f
import sol_engine.ecs.Component

class SolGameComp(
        var gameState: GameState = GameState.BEFORE_START,
        var worldSize: Vector2f = Vector2f()
) : Component() {

    var teamIndexWon: Int = -1

    enum class GameState {
        BEFORE_START,
        RUNNING,
        ENDING,
        ENDED
    }
}