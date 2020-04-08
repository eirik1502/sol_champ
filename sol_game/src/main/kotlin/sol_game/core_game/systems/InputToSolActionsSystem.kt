package sol_game.core_game.systems

import sol_engine.ecs.SystemBase
import sol_engine.input_module.InputComp
import sol_game.core_game.SolActionsPacket
import sol_game.core_game.components.SolActionsPacketComp

class InputToSolActionsSystem : SystemBase() {


    override fun onSetup() {
        usingComponents(InputComp::class.java, SolActionsPacketComp::class.java)
    }

    override fun onUpdate() {
        forEachWithComponents(InputComp::class.java, SolActionsPacketComp::class.java) { _, inpComp, actionsComp ->
            actionsComp.actionsPacket = SolActionsPacket(
                    inpComp.checkAction("mvLeft"),
                    inpComp.checkAction("mvRight"),
                    inpComp.checkAction("mvUp"),
                    inpComp.checkAction("mvDown"),
                    inpComp.checkAction("ability1"),
                    inpComp.checkAction("ability2"),
                    inpComp.checkAction("ability3"),
                    inpComp.floatInput("aimX"),
                    inpComp.floatInput("aimY")
            )
        }
    }
}