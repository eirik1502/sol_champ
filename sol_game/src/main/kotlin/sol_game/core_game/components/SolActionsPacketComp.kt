package sol_game.core_game.components

import sol_engine.ecs.Component
import sol_game.core_game.SolActionsPacket

data class SolActionsPacketComp(
        var actionsPacket: SolActionsPacket = SolActionsPacket()
) : Component()