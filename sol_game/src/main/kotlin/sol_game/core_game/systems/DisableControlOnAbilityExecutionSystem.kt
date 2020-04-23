package sol_game.core_game.systems

import sol_engine.ecs.IteratingSystemBase
import sol_game.core_game.components.AbilityComp
import sol_game.core_game.components.ControlDisabledComp

class DisableControlOnAbilityExecutionSystem : IteratingSystemBase() {
    override fun onSetupWithUpdate() {
        updateWithComponents(
                AbilityComp::class.java,
                ControlDisabledComp::class.java
        ) { _, abComp, controlDisableComp ->
            if (abComp.executingAbilityExecutionTimer > -1) {
                controlDisableComp.disabledTimer = abComp.executingAbilityExecutionTimer
            }
        }
    }
}