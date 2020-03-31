package sol_engine.network.network_ecs;

import sol_engine.core.ModuleSystemBase;
import sol_engine.ecs.Entity;
import sol_engine.network.network_sol_module.NetworkClientModule;
import sol_engine.network.packet_handling.NetworkPacket;
import sol_engine.utils.reflection_utils.ClassUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class NetClientSystem extends ModuleSystemBase {
    @Override
    protected void onSetup() {
        usingComponents(NetClientComp.class);
        usingModules(NetworkClientModule.class);
    }

    @Override
    protected void onStart() {
        NetworkClientModule clientModule = getModule(NetworkClientModule.class);

        List<Class<? extends NetworkPacket>> staticConnectPacketTypes = entitiesStream()
                .map(entity -> entity.getComponent(NetClientComp.class))
                .map(clientComp -> clientComp.staticConnectionPacketType)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        clientModule.usePacketTypes(HostConnectedPacket.class);
        clientModule.usePacketTypes(staticConnectPacketTypes);
    }

    @Override
    protected void onUpdate() {
        NetworkClientModule clientModule = getModule(NetworkClientModule.class);
        forEachWithComponents(NetClientComp.class, (entity, clientComp) -> {
            if (clientComp.staticConnectionPacketType != null && clientComp.staticConnectionPacketHandler != null) {
                clientModule.peekPacketsOfType(clientComp.staticConnectionPacketType).forEach(packet -> {
                    ClassUtils.instanciateNoarg(clientComp.staticConnectionPacketHandler)
                            .handleConnectionPacket(packet, world);
                });
            }

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
