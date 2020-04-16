package sol_game.core_game.systems

import sol_engine.ecs.IteratingSystemBase
import sol_game.core_game.components.FallIntoHoleComp
import sol_game.core_game.components.StockComp

class LoseStockByHoleSystem : IteratingSystemBase() {

    override fun onSetupWithUpdate() {
        updateWithComponents(
                FallIntoHoleComp::class.java,
                StockComp::class.java
        ) { _, fallIntoHoleComp, stockComp ->
            if (fallIntoHoleComp.fallenInHoleNow) {
                stockComp.currentStockCount--
                stockComp.lostStockNow = true
            } else {
                stockComp.lostStockNow = false
            }
        }
    }
}