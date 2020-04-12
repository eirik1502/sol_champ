package sol_game.core_game.components

import org.joml.Vector2f
import sol_engine.ecs.Component
import sol_engine.ecs.Entity

class SceneChildComp(
        var parent: Entity? = null
) : Component() {

    var parentPrevPosition: Vector2f = Vector2f()
    var parentPrevScale: Vector2f = Vector2f(1f, 1f)
    var parentPrevRotationZ: Float = 0f

    override fun copy(other: Component) {
        val otherComp = other as SceneChildComp

        parent = otherComp.parent
        parentPrevPosition.set(otherComp.parentPrevPosition)
        parentPrevScale.set(otherComp.parentPrevScale)
        parentPrevRotationZ = otherComp.parentPrevRotationZ
    }
}