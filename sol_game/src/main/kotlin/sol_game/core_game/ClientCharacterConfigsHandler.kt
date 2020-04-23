package sol_game.core_game

import sol_engine.ecs.World
import sol_engine.network.network_ecs.host_managing.StaticConnectionPacketHandler
import sol_engine.network.packet_handling.NetworkPacket
import sol_game.core_game.entities_factory.CharacterEntities

class ClientCharacterConfigsHandler : StaticConnectionPacketHandler {
    override fun handleConnectionPacket(packet: NetworkPacket?, world: World) {
        packet?.let {
            val charactersConfig = (packet as CharactersConfigsPacket).charactersConfigs
            CharacterEntities.addAllCharactersEntityClasses(false, charactersConfig, world)
        }
    }
}