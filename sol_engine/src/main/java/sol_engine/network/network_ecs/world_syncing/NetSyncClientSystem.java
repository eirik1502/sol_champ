package sol_engine.network.network_ecs.world_syncing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sol_engine.core.ModuleSystemBase;
import sol_engine.ecs.Component;
import sol_engine.ecs.Entity;
import sol_engine.network.network_ecs.host_managing.NetEcsUtils;
import sol_engine.network.network_ecs.host_managing.NetIdComp;
import sol_engine.network.network_ecs.packets.CreateEntityPacket;
import sol_engine.network.network_ecs.packets.RemoveEntityPacket;
import sol_engine.network.network_ecs.packets.UpdateComponentPacket;
import sol_engine.network.network_sol_module.NetworkClientModule;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        getModule(NetworkClientModule.class)
                .usePacketTypes(
                        UpdateComponentPacket.class,
                        CreateEntityPacket.class,
                        RemoveEntityPacket.class
                );
    }

    @Override
    protected void onUpdate() {
        NetworkClientModule clientModule = getModule(NetworkClientModule.class);

        Deque<CreateEntityPacket> createEntityPackets = clientModule.peekPacketsOfType(CreateEntityPacket.class);
        Deque<RemoveEntityPacket> removeEntityPackets = clientModule.peekPacketsOfType(RemoveEntityPacket.class);
        Set<Integer> removeEntitiesNetIds = removeEntityPackets.stream().map(packet -> packet.netId).collect(Collectors.toSet());
        Deque<UpdateComponentPacket> updateComponentPackets = clientModule.peekPacketsOfType(UpdateComponentPacket.class);
        Map<Integer, List<UpdateComponentPacket>> updateCompPacketsByNetId = updateComponentPackets.stream()
                .collect(Collectors.groupingBy(packet -> packet.netId));

        // create new entities
        Stream<Entity> newHostEntitiesStream = createEntityPackets.stream()
                .map(packet -> NetEcsUtils.addEntityFromPacket(packet, world));

        // update components and remove existing entities and new host entities
        Stream.concat(entitiesStream(), newHostEntitiesStream).forEach(entity -> {
            int netId = entity.getComponent(NetIdComp.class).id;
            updateCompPacketsByNetId.getOrDefault(netId, EMPTY_PACKET_LIST).forEach(packet -> {
                NetEcsUtils.updateComponentsFromPacket(packet, entity);
            });

            if (removeEntitiesNetIds.contains(netId)) {
                world.removeEntity(entity);
            }
        });
    }
}
