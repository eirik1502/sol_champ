package sol_game.core_game

import org.joml.Vector2f
import sol_engine.ecs.Component
import sol_engine.ecs.Entity

class SceneChildComp(
        var parent: Entity? = null
) : Component() {

    var parentPrevPosition: Vector2f = Vector2f()
    var parentPrevScale: Vector2f = Vector2f(1f, 1f)
    var parentPrevRotationZ: Float = 0f

    override fun clone(): SceneChildComp {
        val comp = super.cloneAs(SceneChildComp::class.java)
        comp.parentPrevPosition = Vector2f(parentPrevPosition)
        comp.parentPrevScale = Vector2f(parentPrevScale)
        return comp
    }
}