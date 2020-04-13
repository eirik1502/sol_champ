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

import java.util.*;
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


    private Map<GameHost, Entity> entityHosts = new HashMap<>();

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

            handleNewDisconnectedHosts(serverMod.getNewDisconnectedHosts());
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

            // add the new host
            entityHosts.put(newHost, newHostEntity);
        });
    }

    private void handleNewDisconnectedHosts(Set<GameHost> disconnectedHosts) {
        disconnectedHosts.forEach(host -> {
            if (entityHosts.containsKey(host)) {
                Entity removeEntityHost = entityHosts.remove(host);
                world.removeEntity(removeEntityHost);
            } else {
                logger.warn("A disconnecting host is not registered as connected");
            }
        });

    }
}
