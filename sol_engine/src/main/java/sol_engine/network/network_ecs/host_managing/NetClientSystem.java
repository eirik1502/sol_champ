package sol_engine.network.network_ecs.host_managing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sol_engine.core.ModuleSystemBase;
import sol_engine.network.network_sol_module.NetworkClientModule;
import sol_engine.utils.reflection_utils.ClassUtils;

public class NetClientSystem extends ModuleSystemBase {
    private final Logger logger = LoggerFactory.getLogger(NetClientSystem.class);

    @Override
    protected void onSetup() {
        usingComponents(NetClientComp.class);
        usingModules(NetworkClientModule.class);
    }

    @Override
    protected void onSetupEnd() {
//        NetworkClientModule clientModule = getModule(NetworkClientModule.class);
//        clientModule.usePacketTypes(CreateHostEntityPacket.class);

//        List<Class<? extends NetworkPacket>> staticConnectPacketTypes = entitiesStream()
//                .map(entity -> entity.getComponent(NetClientComp.class))
//                .map(clientComp -> clientComp.staticConnectionPacketType)
//                .filter(Objects::nonNull)
//                .collect(Collectors.toList());
//        clientModule.usePacketTypes(staticConnectPacketTypes);
    }

    @Override
    protected void onStart() {

    }

    @Override
    protected void onUpdate() {
        NetworkClientModule clientModule = getModule(NetworkClientModule.class);
        forEachWithComponents(NetClientComp.class, (entity, clientComp) -> {

            // handle static connect packets
            if (clientComp.staticConnectionPacketType != null && clientComp.staticConnectionPacketHandler != null) {
                clientModule.peekPacketsOfType(clientComp.staticConnectionPacketType).forEach(packet -> {
                    ClassUtils.instantiateNoargs(clientComp.staticConnectionPacketHandler)
                            .handleConnectionPacket(packet, world);
                    logger.info("Static connection packet handled");
                });
            }

//            clientModule.peekPacketsOfType(CreateHostEntityPacket.class)
//                    .forEach(connectPacket -> {
//                        Entity newHostEntity = NetEcsUtils.addEntityForHost(
//                                false,
//                                connectPacket.host,
//                                connectPacket.netId,
//                                connectPacket.entityClass,
//                                connectPacket.updateComponents,
//                                world
//                        );
//                        logger.info("Entiity created for a HostConnectedPacket, entity: " + newHostEntity + " packet: " + connectPacket);
//                    });
        });
    }
}
