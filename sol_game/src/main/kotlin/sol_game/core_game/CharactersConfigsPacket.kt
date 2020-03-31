package sol_game.core_game

import sol_engine.network.packet_handling.NetworkPacket

data class CharactersConfigsPacket(
        val charactersConfigs: List<CharacterConfig>
) : NetworkPacket