package sol_engine.network.network_ecs.host_managing;

import org.joml.Vector2f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sol_engine.core.ModuleSystemBase;
import sol_engine.core.TransformComp;
import sol_engine.ecs.Component;
import sol_engine.ecs.Entity;
import sol_engine.network.network_ecs.EntityHost;
import sol_engine.network.network_ecs.packets.CreateHostEntityPacket;
import sol_engine.network.network_ecs.packets.HostConnectedPacket;
import sol_engine.network.network_ecs.world_syncing.NetSyncComp;
import sol_engine.network.network_ecs.world_syncing.NetSyncServerSystem;
import sol_engine.network.network_game.GameHost;
import sol_engine.network.network_sol_module.NetworkServerModule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


/**
 * Handles connected clients.
 * First sends a staticConnectionPacket if present in the {@link NetServerComp}.
 * Note that this packet type is not assigned to the {@link NetworkServerModule} by this system.
 * Then creates an entity for the connected client and gives it a {@link NetHostComp}.
 * the {@link sol_engine.ecs.EntityClass} of the entity should have a {@link NetSyncComp}
 * with {@code syncAdd} and {@code syncRemove} set to {@code true}.
 * The entity is further synced in the {@link NetSyncServerSystem} that should also be added.
 */
public class NetServerSystem extends ModuleSystemBase {
    private final Logger logger = LoggerFactory.getLogger(NetServerSystem.class);
    private int nextEntityNetId = 0;
    private Map<Entity, EntityHost> entityHosts = new HashMap<>();

    @Override
    protected void onSetup() {
        usingComponents(NetServerComp.class);
        usingModules(NetworkServerModule.class);
    }

    @Override
    public void onStart() {
        NetworkServerModule serverModule = getModule(NetworkServerModule.class);
//        serverModule.usePacketTypes(CreateHostEntityPacket.class);
    }

    @Override
    protected void onUpdate() {
        NetworkServerModule serverMod = getModule(NetworkServerModule.class);

        if (entities.size() != 1) {
            logger.warn("There should only be one entity with NetServerComp, current count: " + entities.size());
        }

        forEachWithComponents(NetServerComp.class, (entity, netServerComp) -> {

            handleNewHosts(serverMod, netServerComp);

            //TODO: handle disconnecting hosts
        });
    }

    private void handleNewHosts(NetworkServerModule serverModule, NetServerComp netServerComp) {
        serverModule.getNewConnectedHosts().forEach(newHost -> {
            if (netServerComp.staticConnectionPacket != null) {
                // reply with the static connect packet
                serverModule.sendPacket(netServerComp.staticConnectionPacket, newHost);
            }

            EntityHostStartData newEntityData = netServerComp.hostEntitiesStartData
                    .get(newHost.teamIndex).get(newHost.playerIndex);
            String entityClass = newEntityData.entityClass;
            Set<Component> modifyComponents = newEntityData.modifyComponents;
            Set<Component> createComponents = Set.of(
                    new NetHostComp(newHost),
                    new TeamPlayerComp(newHost.teamIndex, newHost.playerIndex)
            );
            Entity newHostEntity = NetEcsUtils.addEntityForHost(
                    true,
                    newHost,
                    entityClass,
                    modifyComponents,
                    createComponents,
                    world
            );

            // let NetSyncServerSystem create a NetHostComp on the client side
            newHostEntity.modifyIfHasComponent(NetSyncComp.class, comp ->
                    comp.createComponentTypesOnAdd.addAll(Set.of(
                            NetHostComp.class,
                            TeamPlayerComp.class
                    ))
            );


//            // send the new client to all previously connected hosts
//            // the new host is not present in this map yet
//            CreateHostEntityPacket createNewHostEntityPacket = new CreateHostEntityPacket(
//                    newHost,
//                    newNetId,
//                    entityClass,
//                    List.of(),
//                    modifyComponents
//            );
//            List<GameHost> connectedHosts = entityHosts.values().stream()
//                    .map(entityHost -> entityHost.host)
//                    .collect(Collectors.toList());
//            serverModule.sendPacket(createNewHostEntityPacket, connectedHosts);

            // add the new host
            entityHosts.put(newHostEntity, new EntityHost(newHostEntity, newHost, entityClass));


            // send all connected hosts to the new client, including itself.
            // World is not updated with the new component yet, but it is present in entityHosts
//            entityHosts.values().forEach(entityHost -> {
//                CreateHostEntityPacket createHostEntityPacket = new CreateHostEntityPacket(
//                        entityHost.host,
//                        entityHost.entity.getComponent(NetIdComp.class).id,
//                        entityHost.entityClass,
//                        List.of(),
//                        modifyComponentTypes.stream()
//                                .map(compType -> entityHost.entity.getComponent(compType))
//                                .collect(Collectors.toList())
//                );
//                serverModule.sendPacket(
//                        createHostEntityPacket,
//                        newHost
//                );
//            });
        });
    }

    private int createEntityNetId() {
        return nextEntityNetId++;
    }
}
