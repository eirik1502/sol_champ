package sol_game

import org.joml.Vector2f
import sol_engine.ecs.Component

class HurtboxComp : Component() {
    val currDamageTakenVecs: MutableList<Vector2f> = ArrayList()
    var totalDamageTaken: Float = 0f

    override fun clone(): HurtboxComp {
        val comp = HurtboxComp()
        comp.currDamageTakenVecs.addAll(currDamageTakenVecs)
        comp.totalDamageTaken = totalDamageTaken
        return comp
    }
}