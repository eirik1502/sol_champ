package sol_game.core_game.systems

import org.joml.Vector2f
import sol_engine.ecs.SystemBase
import sol_engine.physics_module.PhysicsBodyComp
import sol_engine.utils.math.MathF
import sol_game.core_game.components.ControlDisabledComp
import sol_game.core_game.components.HurtboxComp

class HurtboxSystem : SystemBase() {
    override fun onSetup() {
        usingComponents(HurtboxComp::class.java, PhysicsBodyComp::class.java)
    }

    override fun onUpdate() {
        forEachWithComponents(
                HurtboxComp::class.java,
                PhysicsBodyComp::class.java
        )
        { _, hurtboxComp, physicsComp ->
            if (hurtboxComp.currHitsTaken.isNotEmpty()) {
                val damageTakenNow = hurtboxComp.currHitsTaken.fold(0f) { acc, hit ->
                    acc + hit.damage
                }

                val totalDamageTakenNow = hurtboxComp.totalDamageTaken + damageTakenNow

                val knockbackVec = hurtboxComp.currHitsTaken.fold(Vector2f()) { acc, hit ->
                    val knockback = calculateKnockback(totalDamageTakenNow, hit.baseKnockback, hit.knockbackRatio)
                    val normalizedInteractionVec =
                            if (hit.interactionVector.lengthSquared() == 0f) Vector2f(1f, 0f)
                            else hit.interactionVector.normalize(Vector2f())
                    val singleKnockbackVec = normalizedInteractionVec.mul(knockback)
                    acc.add(singleKnockbackVec)
                }

                hurtboxComp.totalDamageTaken = MathF.min(totalDamageTakenNow, 9999f)

                physicsComp.impulse.add(knockbackVec)

                hurtboxComp.currHitsTaken.clear()
                hurtboxComp.damageTakenNow = damageTakenNow
                hurtboxComp.knockbackNow = knockbackVec
            } else {
                hurtboxComp.damageTakenNow = 0f
                hurtboxComp.knockbackNow = Vector2f()
            }
        }
    }

    private fun calculateKnockback(totalDamge: Float, baseKnockback: Float, knockbackRatio: Float): Float {
        return totalDamge * knockbackRatio + baseKnockback
    }


}