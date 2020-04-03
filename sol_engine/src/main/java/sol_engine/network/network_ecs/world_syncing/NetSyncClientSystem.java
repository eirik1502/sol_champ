package sol_engine.network.network_ecs.world_syncing;

import sol_engine.core.ModuleSystemBase;
import sol_engine.core.TransformComp;
import sol_engine.network.network_ecs.host_managing.NetIdComp;
import sol_engine.network.network_ecs.packets.NetTransformPacket;
import sol_engine.network.network_sol_module.NetworkClientModule;

import java.util.Deque;

public class NetTransformClientSystem extends ModuleSystemBase {
    @Override
    protected void onSetup() {
        usingComponents(NetIdComp.class, NetTransformComp.class, TransformComp.class);
        usingModules(NetworkClientModule.class);
    }

    @Override
    protected void onSetupEnd() {
        getModule(NetworkClientModule.class).usePacketTypes(NetTransformPacket.class);
    }

    @Override
    protected void onUpdate() {
        NetworkClientModule clientModule = getModule(NetworkClientModule.class);
        Deque<NetTransformPacket> transformPackets = clientModule.peekPacketsOfType(NetTransformPacket.class);
        forEachWithComponents(
                NetIdComp.class,
                NetTransformComp.class,
                TransformComp.class,
                (entity, netIdComp, netTransformComp, transComp) -> {
                    if (!transformPackets.isEmpty()) {
                        transformPackets.peekLast().toTransformComp(transComp);
                    }
                }
        );
    }
}
