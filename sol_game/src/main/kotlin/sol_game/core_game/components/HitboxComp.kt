package sol_game.core_game.components

import sol_engine.ecs.Component
import sol_engine.ecs.Entity

class HitboxComp(
        var damage: Float = 10f,
        var baseKnockback: Float = 0f,
        var knockbackRatio: Float = 0f,
        var owner: Entity? = null
) : Component() {
    var currDamageDealt: Float = 0f

    override fun copy(fromComp: Component?) {
        val otherComp = fromComp as HitboxComp
        
        damage = otherComp.damage
        baseKnockback = otherComp.baseKnockback
        knockbackRatio = otherComp.knockbackRatio
        owner = otherComp.owner
        currDamageDealt = otherComp.currDamageDealt
    }
}