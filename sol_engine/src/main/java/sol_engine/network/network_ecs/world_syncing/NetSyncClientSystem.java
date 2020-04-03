package sol_engine.network.network_ecs.world_syncing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sol_engine.core.ModuleSystemBase;
import sol_engine.ecs.Component;
import sol_engine.ecs.Entity;
import sol_engine.network.network_ecs.host_managing.NetIdComp;
import sol_engine.network.network_ecs.packets.UpdateComponentPacket;
import sol_engine.network.network_sol_module.NetworkClientModule;

import java.util.*;
import java.util.stream.Collectors;

public class NetSyncClientSystem extends ModuleSystemBase {
    private final static List<UpdateComponentPacket> EMPTY_PACKET_LIST = Collections.emptyList();

    private static final Logger logger = LoggerFactory.getLogger(NetSyncClientSystem.class);


    @Override
    protected void onSetup() {
        usingComponents(NetIdComp.class, NetSyncComp.class);
        usingModules(NetworkClientModule.class);
    }

    @Override
    protected void onSetupEnd() {
        getModule(NetworkClientModule.class).usePacketTypes(UpdateComponentPacket.class);
    }

    @Override
    protected void onUpdate() {
        NetworkClientModule clientModule = getModule(NetworkClientModule.class);

        Deque<UpdateComponentPacket> updateComponentPackets = clientModule.peekPacketsOfType(UpdateComponentPacket.class);

        Map<Integer, List<UpdateComponentPacket>> updateCompPacketsByNetId = updateComponentPackets.stream()
                .collect(Collectors.groupingBy(packet -> packet.netId));

        forEachWithComponents(
                NetIdComp.class,
                NetSyncComp.class,
                (entity, netIdComp, netSyncComp) -> {

                    int netId = netIdComp.id;
                    Set<Class<? extends Component>> acceptedSyncComponents = netSyncComp.syncComponentTypes;

                    updateCompPacketsByNetId.getOrDefault(netId, EMPTY_PACKET_LIST).forEach(packet -> {
                        Component targetComp = packet.component;
                        Class<? extends Component> compType = targetComp.getClass();

                        if (acceptedSyncComponents.contains(compType)) {
                            entity.modifyIfHasComponent(compType, comp -> comp.copy(targetComp));
                        } else {
                            logger.warn("Trying to sync a component that is not present in NetSyncComp." +
                                    " CompType: " + targetComp + ", Entity: " + entity);
                        }
                    });
                }
        );
    }
}
