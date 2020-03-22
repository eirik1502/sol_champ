package sol_game.core_game.systems

import sol_engine.core.ModuleSystemBase
import sol_engine.core.TransformComp
import sol_engine.network.network_sol_module.NetworkServerModule
import sol_game.core_game.NetGameState
import sol_game.core_game.NetPlayerState
import sol_game.core_game.components.CharacterComp
import sol_game.core_game.components.NetComp

class ServerNetworkOutputSystem() : ModuleSystemBase() {

    override fun onSetup() {
        usingComponents(
                CharacterComp::class.java,
                NetComp::class.java,
                TransformComp::class.java
        )
        usingModules(
                NetworkServerModule::class.java
        )
    }

    override fun onUpdate() {
        val serverModule = getModule(NetworkServerModule::class.java)
        val playerStatePackets =
                entities.map { entity ->
                    val netComp = entity.getComponent(NetComp::class.java)
                    val transComp = entity.getComponent(TransformComp::class.java)
                    NetPlayerState(
                            netComp.id,
                            transComp.x,
                            transComp.y,
                            transComp.rotationZ
                    )
                }
        val gameStatePacket = NetGameState(playerStatePackets)
        serverModule.sendPacketAll(gameStatePacket)
    }
}