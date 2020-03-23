package sol_game.core_game.systems

import sol_engine.core.ModuleSystemBase
import sol_engine.network.network_sol_module.NetworkClientModule
import sol_game.core_game.SolActionsPacket
import sol_game.core_game.components.SolActionsPacketComp


/**
 * Sends the sol actions stored in all SolActionPacketComp's to the server
 * There should usually exist only one such comp
 */
class ClientNetworkOutputSystem() : ModuleSystemBase() {

    override fun onSetup() {
        usingComponents(SolActionsPacketComp::class.java)
        usingModules(NetworkClientModule::class.java)
    }

    override fun onStart() {
        getModule(NetworkClientModule::class.java).usePacketTypes(SolActionsPacket::class.java)
    }

    override fun onUpdate() {
        forEachWithComponents(SolActionsPacketComp::class.java) { _, actionsPacketComp ->
            getModule(NetworkClientModule::class.java).sendPacket(actionsPacketComp.actionsPacket)
        }
    }
}