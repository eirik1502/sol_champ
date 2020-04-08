package sol_game.core_game.components

import sol_engine.ecs.Component
import sol_game.core_game.SolGameStatePacket

data class SolStatePacketComp(
        var statePacket: SolGameStatePacket = SolGameStatePacket()
) : Component()