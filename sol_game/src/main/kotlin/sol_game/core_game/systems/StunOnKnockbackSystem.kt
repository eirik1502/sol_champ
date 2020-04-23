package sol_game.core_game.systems

import sol_engine.ecs.IteratingSystemBase
import sol_game.core_game.components.AbilityComp
import sol_game.core_game.components.ControlDisabledComp
import sol_game.core_game.components.HurtboxComp

class StunOnKnockbackSystem : IteratingSystemBase() {
    override fun onSetupWithUpdate() {
        updateWithComponents(
                HurtboxComp::class.java,
                ControlDisabledComp::class.java,
                AbilityComp::class.java
        ) { _, hurtboxComp, controlDisabledComp, abilityComp ->
            if (hurtboxComp.knockbackNow.lengthSquared() != 0f) {
                abilityComp.triggerInterrupt = true
                val stunTime = calculateStunTime(hurtboxComp.knockbackNow.length())
                controlDisabledComp.disabledTimer = stunTime
            }

        }
    }

    private fun calculateStunTime(knockback: Float): Int {
        return (knockback / 60f).toInt()
    }
}