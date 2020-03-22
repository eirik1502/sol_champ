package sol_game.core_game

import sol_engine.network.network_input.NetInputPacket

class SolInputPacket(
        val mvLeft: Boolean = false,
        val mvRight: Boolean = false,
        val mvUp: Boolean = false,
        val mvDown: Boolean = false,
        val ability1: Boolean = false,
        val ability2: Boolean = false,
        val ability3: Boolean = false,
        val aimX: Float = 0f,
        val aimY: Float = 0f
) : NetInputPacket() {

    override fun toString(): String {
        return "SolInputPacket(mvLeft=$mvLeft, mvRight=$mvRight, mvUp=$mvUp, mvDown=$mvDown, ability1=$ability1, ability2=$ability2, ability3=$ability3, aimX=$aimX, aimY=$aimY)"
    }
}