package sol_game.core_game.systems

import org.joml.Vector2f
import sol_engine.ecs.SystemBase
import sol_engine.physics_module.PhysicsBodyComp
import sol_game.core_game.components.HurtboxComp

class KnockbackSystem : SystemBase() {
    override fun onSetup() {
        usingComponents(HurtboxComp::class.java, PhysicsBodyComp::class.java)
    }

    override fun onUpdate() {
        forEachWithComponents(HurtboxComp::class.java, PhysicsBodyComp::class.java)
        { _, hurtboxComp, physicsComp ->
            val impulse = hurtboxComp.currHitsTaken.fold(Vector2f())
            { acc, hit ->
                val knockback = calculateKnockback(hurtboxComp.totalDamageTaken, hit.baseKnockback, hit.knockbackRatio)
                acc.add(hit.direction.normalize().mul(knockback))
            }

            physicsComp.impulse.add(impulse)
            hurtboxComp.currHitsTaken.clear()
        }
    }

    private fun calculateKnockback(totalDamge: Float, baseKnockback: Float, knockbackRatio: Float): Float {
        return totalDamge * knockbackRatio + baseKnockback
    }
}