package sol_game.core_game

import sol_engine.network.packet_handling.NetworkPacket

data class SolGameStatePacket(
        val playersState: List<SolPlayerStatePacket> = listOf()
) : NetworkPacket

data class SolPlayerStatePacket(
        val netId: Int = -1,
        val posX: Float = 0f,
        val posY: Float = 0f,
        val rotation: Float = 0f
) : NetworkPacket