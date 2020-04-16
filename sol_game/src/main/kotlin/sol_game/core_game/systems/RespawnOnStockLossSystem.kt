package sol_game.core_game.systems

import org.joml.Vector2f
import sol_engine.core.TransformComp
import sol_engine.ecs.IteratingSystemBase
import sol_engine.physics_module.PhysicsBodyComp
import sol_game.core_game.components.SpawnPositionComp
import sol_game.core_game.components.StockComp

class RespawnOnStockLossSystem : IteratingSystemBase() {
    override fun onSetupWithUpdate() {
        updateWithComponents(
                TransformComp::class.java,
                SpawnPositionComp::class.java,
                StockComp::class.java,
                PhysicsBodyComp::class.java
        ) { _, transComp, spawnPosComp, stockComp, physComp ->
            if (stockComp.lostStockNow) {
                transComp.position.set(spawnPosComp.spawnPosition)
                physComp.velocity.set(Vector2f())
                physComp.acceleration.set(Vector2f())
            }
        }
    }
}