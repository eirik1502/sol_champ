package sol_engine.network.network_ecs;

import sol_engine.core.ModuleSystemBase;
import sol_engine.ecs.Entity;
import sol_engine.network.network_sol_module.NetworkClientModule;

public class NetClientSystem extends ModuleSystemBase {
    @Override
    protected void onSetup() {
        usingComponents(NetClientComp.class);
        usingModules(NetworkClientModule.class);
    }

    @Override
    protected void onStart() {
        getModule(NetworkClientModule.class).usePacketTypes(HostConnectedPacket.class);
    }

    @Override
    protected void onUpdate() {
        NetworkClientModule clientModule = getModule(NetworkClientModule.class);
        forEachWithComponents(NetClientComp.class, (entity, clientComp) -> {
            clientModule.peekPacketsOfType(HostConnectedPacket.class)
                    .forEach(connectPacket -> {
                        Entity newHostEntity = NetEcsUtils.addEntityForHost(
                                false,
                                connectPacket.host,
                                connectPacket.entityClass,
                                connectPacket.startPos,
                                world
                        );
                    });
        });
    }
}
