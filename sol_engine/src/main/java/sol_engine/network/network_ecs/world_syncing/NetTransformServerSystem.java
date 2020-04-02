package sol_engine.network.network_ecs.world_syncing;

import sol_engine.core.ModuleSystemBase;
import sol_engine.core.TransformComp;
import sol_engine.network.network_ecs.packets.NetTransformPacket;
import sol_engine.network.network_sol_module.NetworkServerModule;

public class NetTransformServerSystem extends ModuleSystemBase {
    @Override
    protected void onSetup() {
        usingComponents(NetIdComp.class, NetTransformComp.class, TransformComp.class);
        usingModules(NetworkServerModule.class);

    }

    @Override
    protected void onSetupEnd() {
        getModule(NetworkServerModule.class).usePacketTypes(NetTransformPacket.class);
    }

    @Override
    protected void onStart() {

    }

    @Override
    protected void onUpdate() {
        NetworkServerModule serverModule = getModule(NetworkServerModule.class);
        forEachWithComponents(
                NetIdComp.class,
                NetTransformComp.class,
                TransformComp.class,
                (entity, netIdComp, netTransformComp, transComp) -> {
                    serverModule.sendPacket(new NetTransformPacket(transComp), netIdComp.gameHost);
                });
    }
}
