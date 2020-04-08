package sol_game.core_game

import sol_engine.ecs.World
import sol_engine.network.network_ecs.host_managing.StaticConnectionPacketHandler
import sol_engine.network.packet_handling.NetworkPacket

class ClientCharacterConfigsHandler : StaticConnectionPacketHandler {
    override fun handleConnectionPacket(packet: NetworkPacket?, world: World) {
        packet?.let {
            val charactersConfig = (packet as CharactersConfigsPacket).charactersConfigs
            addAllCharactersEntityClasses(false, charactersConfig, world)
            println("added character classes!: $charactersConfig")
        }
    }
}