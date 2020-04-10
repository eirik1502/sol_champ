package sol_game.core_game.components

import sol_engine.ecs.Component

class SolGameComp(
        var gameStarted: Boolean = false,
        var gameEnded: Boolean = false,
        var teamIndexWon: Int = -1
) : Component()