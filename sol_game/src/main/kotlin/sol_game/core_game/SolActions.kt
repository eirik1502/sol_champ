package sol_game.core_game

import sol_engine.network.network_input.NetInputPacket

data class SolActions(
        val mvLeft: Boolean = false,
        val mvRight: Boolean = false,
        val mvUp: Boolean = false,
        val mvDown: Boolean = false,
        val ability1: Boolean = false,
        val ability2: Boolean = false,
        val ability3: Boolean = false,
        val aimX: Float = 0f,
        val aimY: Float = 0f
) : NetInputPacket()