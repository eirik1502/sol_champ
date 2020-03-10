package sol_game.core_game

import sol_engine.network.network_input.NetInputPacket

class SolInputPacket(
        val mvLeft: Boolean,
        val mvRight: Boolean,
        val mvUp: Boolean,
        val mvDown: Boolean,
        val ability1: Boolean,
        val ability2: Boolean,
        val ability3: Boolean,
        val aimX: Float,
        val aimY: Float
) : NetInputPacket() {
}