package sol_game.core_game.systems

import sol_engine.core.ModuleSystemBase
import sol_engine.ecs.Component
import sol_engine.ecs.SystemBase
import sol_engine.network.network_sol_module.NetworkClientModule
import sol_game.core_game.SolGameStatePacket
import sol_game.core_game.components.SolStatePacketComp

/**
 * Retrieve the new state sendt from the server
 * Stores the state in all SolStatePacketComps
 */
class ClientNetworkInputSystem : ModuleSystemBase() {
    override fun onSetup() {
        usingComponents(SolStatePacketComp::class.java)
        usingModules(NetworkClientModule::class.java)
    }

    override fun onSetupEnd() {
        getModule(NetworkClientModule::class.java).usePacketTypes(SolGameStatePacket::class.java)
    }

    override fun onStart() {
    }

    override fun onUpdate() {
        forEachWithComponents(SolStatePacketComp::class.java) { _, statePacketComp ->
            val currPackets = getModule(NetworkClientModule::class.java)
                    .peekPacketsOfType(SolGameStatePacket::class.java)
            if (!currPackets.isEmpty()) {
                statePacketComp.statePacket = currPackets.last
            }
        }
    }

}