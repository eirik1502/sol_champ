package sol_game

import org.joml.Vector2f
import sol_engine.ecs.Component

class HurtboxComp : Component() {
    data class Hit(val direction: Vector2f = Vector2f(),
                   val damage: Float = 0f,
                   val baseKnockback: Float = 0f,
                   val knockbackRatio: Float = 0f)

    val currHitsTaken: MutableList<Hit> = ArrayList()
    var totalDamageTaken: Float = 0f

    override fun clone(): HurtboxComp {
        val comp = HurtboxComp()
        comp.currHitsTaken.addAll(currHitsTaken)
        comp.totalDamageTaken = totalDamageTaken
        return comp
    }
}