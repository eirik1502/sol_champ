package sol_game.core_game.components

import sol_engine.ecs.Component

class StockComp(
        var startingStockCount: Int = 3
) : Component() {
    var currentStockCount: Int = startingStockCount
    var lostStockNow: Boolean = false
}