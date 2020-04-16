package sol_game.core_game.components

import org.joml.Vector2f
import sol_engine.ecs.Component

data class SpawnPositionComp(
        val spawnPosition: Vector2f = Vector2f()
) : Component() {
    override fun copy(fromComp: Component) {
        (fromComp as SpawnPositionComp).let {
            spawnPosition.set(it.spawnPosition)
        }
    }
}