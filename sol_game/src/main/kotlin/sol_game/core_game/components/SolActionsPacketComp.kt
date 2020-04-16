package sol_game.core_game.components

import sol_engine.ecs.Component
import sol_game.core_game.SolActions

data class SolActionsPacketComp(
        var actions: SolActions = SolActions()
) : Component() {

    override fun copy(fromComp: Component) {
        val otherComp = fromComp as SolActionsPacketComp
        actions = otherComp.actions.copy()
    }
}