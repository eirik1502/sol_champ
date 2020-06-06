package sol_game.core_game.systems

import org.joml.Vector2f
import sol_engine.core.TransformComp
import sol_engine.ecs.IteratingSystemBase
import sol_engine.ecs.SystemBase
import sol_engine.physics_module.PhysicsBodyComp
import sol_game.core_game.components.CharacterComp
import sol_game.core_game.components.SpawnPositionComp
import sol_game.core_game.components.StockComp

class RespawnOnStockLossSystem : SystemBase() {

    override fun onSetup() {
        usingComponents(
                TransformComp::class.java,
                SpawnPositionComp::class.java,
                StockComp::class.java,
                PhysicsBodyComp::class.java,
                CharacterComp::class.java
        )
    }

    override fun onUpdate() {
        val anyCharacterLostStock = entities
                .find { it.getComponent(StockComp::class.java).lostStockNow } != null

        if (anyCharacterLostStock) {

            forEachWithComponents(
                    TransformComp::class.java,
                    SpawnPositionComp::class.java,
                    PhysicsBodyComp::class.java
            ) { _, transComp, spawnPosComp, physComp ->
                transComp.position.set(spawnPosComp.spawnPosition)
                physComp.velocity.set(Vector2f())
                physComp.acceleration.set(Vector2f())
            }
        }
    }

}