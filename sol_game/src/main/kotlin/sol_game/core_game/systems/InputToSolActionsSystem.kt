package sol_game.core_game.systems

import sol_engine.ecs.SystemBase
import sol_engine.input_module.InputComp
import sol_game.core_game.SolActions
import sol_game.core_game.components.SolActionsPacketComp

class InputToSolActionsSystem : SystemBase() {


    override fun onSetup() {
        usingComponents(InputComp::class.java, SolActionsPacketComp::class.java)
    }

    override fun onUpdate() {
        forEachWithComponents(InputComp::class.java, SolActionsPacketComp::class.java) { _, inpComp, actionsComp ->
            actionsComp.actions = SolActions(
                    inpComp.checkTrigger("mvLeft"),
                    inpComp.checkTrigger("mvRight"),
                    inpComp.checkTrigger("mvUp"),
                    inpComp.checkTrigger("mvDown"),
                    inpComp.checkTrigger("ability1"),
                    inpComp.checkTrigger("ability2"),
                    inpComp.checkTrigger("ability3"),
                    inpComp.floatInput("aimX"),
                    inpComp.floatInput("aimY")
            )
        }
    }
}