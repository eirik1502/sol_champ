package sol_game.core_game.systems

import sol_engine.ecs.IteratingSystemBase
import sol_game.core_game.components.HurtboxComp
import sol_game.core_game.components.StockComp

class ResetDamageOnStockLossSystem : IteratingSystemBase() {
    override fun onSetupWithUpdate() {
        updateWithComponents(
                StockComp::class.java,
                HurtboxComp::class.java
        ) { _, stockComp, hurtboxComp ->
            if (stockComp.lostStockNow) {
                hurtboxComp.totalDamageTaken = 0f;
            }
        }
    }
}