package sol_game.core_game.components

import sol_engine.ecs.Component
import sol_engine.ecs.Entity

class HitboxComp(
        var damage: Float = 10f,
        var baseKnockback: Float = 0f,
        var knockbackRatio: Float = 0f,
        var knockbackPoint: Float = 0f,
        var knockbackTowardsPoint: Boolean = false,
        var owner: Entity? = null
) : Component() {
    var hitsGivenNow: MutableList<HurtboxComp.Hit> = mutableListOf()

    override fun copy(fromComp: Component?) {
        val otherComp = fromComp as HitboxComp

        damage = otherComp.damage
        baseKnockback = otherComp.baseKnockback
        knockbackRatio = otherComp.knockbackRatio
        owner = otherComp.owner
        hitsGivenNow = otherComp.hitsGivenNow.toMutableList()
        knockbackPoint = otherComp.knockbackPoint
        knockbackTowardsPoint = otherComp.knockbackTowardsPoint
    }
}