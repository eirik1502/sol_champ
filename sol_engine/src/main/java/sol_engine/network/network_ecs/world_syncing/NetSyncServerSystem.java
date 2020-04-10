package sol_engine.network.network_ecs.world_syncing;

import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sol_engine.core.ModuleSystemBase;
import sol_engine.ecs.Component;
import sol_engine.ecs.Entity;
import sol_engine.ecs.listeners.EntityListener;
import sol_engine.network.network_ecs.host_managing.ClientControlledComp;
import sol_engine.network.network_ecs.host_managing.NetEcsUtils;
import sol_engine.network.network_ecs.host_managing.NetHostComp;
import sol_engine.network.network_ecs.host_managing.NetIdComp;
import sol_engine.network.network_ecs.packets.CreateEntityPacket;
import sol_engine.network.network_ecs.packets.RemoveEntityPacket;
import sol_engine.network.network_ecs.packets.UpdateComponentPacket;
import sol_engine.network.network_game.GameHost;
import sol_engine.network.network_sol_module.NetworkServerModule;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class NetSyncServerSystem extends ModuleSystemBase {
    private static Logger logger = LoggerFactory.getLogger(NetSyncServerSystem.class);

    private int nextEntityNetId = 0;
    private Set<Entity> prevEntities = new HashSet<>();
    private List<GameHost> connectedHosts = new ArrayList<>();

    @Override
    protected void onSetup() {
        usingComponents(NetSyncComp.class);
        usingModules(NetworkServerModule.class);
    }

    @Override
    protected void onSetupEnd() {
        getModule(NetworkServerModule.class)
                .usePacketTypes(
                        UpdateComponentPacket.class,
                        CreateEntityPacket.class,
                        RemoveEntityPacket.class
                );
    }

    @Override
    protected void onStart() {
        prevEntities.addAll(entities.copyToList());

        // hack because components can't be added after entity is added
        world.listeners.addEntityListener(EntityListener.WillBeAdded.class, ((entity, world1) -> {
            if (entity.hasComponent(NetSyncComp.class)) {
                entity.addComponent(new NetIdComp());
            }
        }));
    }

    @Override
    protected void onUpdate() {
        NetworkServerModule serverModule = getModule(NetworkServerModule.class);

        // handle added and removed entities
        Set<Entity> currEntities = entities.copyToSet(new HashSet<>());
        if (!currEntities.equals(prevEntities)) {
            Sets.SetView<Entity> removedEntities = Sets.difference(prevEntities, currEntities);
            Sets.SetView<Entity> addedEntities = Sets.difference(currEntities, prevEntities);

            // handle added entities
            addedEntities.forEach(entity -> handleEntityAdded(entity, entity.getComponent(NetSyncComp.class), serverModule));

            // handle removed entities
            removedEntities.forEach(entity -> handleEntityRemoved(entity, entity.getComponent(NetSyncComp.class), serverModule));

            // send all entities to new Host entities (including itself)
            // should happen after added and removed entites so new entities are not sendt twice
            List<Entity> newHostEntities = addedEntities.stream()
                    .filter(entity -> entity.hasComponent(NetHostComp.class))
                    .collect(Collectors.toList());

            List<Entity> removedHostEntities = removedEntities.stream()
                    .filter(entity -> entity.hasComponent(NetHostComp.class))
                    .collect(Collectors.toList());

            handleHostEntitiesAdded(newHostEntities, serverModule);

            connectedHosts.addAll(newHostEntities.stream()
                    .map(entity -> entity.getComponent(NetHostComp.class).host)
                    .collect(Collectors.toSet()));

            connectedHosts.removeAll(removedHostEntities.stream()
                    .map(removedHostEntity -> removedHostEntity.getComponent(NetHostComp.class).host)
                    .collect(Collectors.toSet()));


            prevEntities = currEntities;
        }

        // handle components to be updated
        forEachWithComponents(
                NetIdComp.class,
                NetSyncComp.class,
                (entity, netIdComp, netSyncComp) -> {
                    syncComponents(netIdComp.id, entity, netSyncComp.syncComponentTypes, serverModule);
                });
    }

    private void handleEntityAdded(Entity entity, NetSyncComp netSyncComp, NetworkServerModule serverModule) {
        // add a netIdComp
        int newNetId = createEntityNetId();

        // we would like to create the component here, but we fall abck on a hack to add the component when an entity is added, then update it here
//        entity.addComponent(new NetIdComp(newNetId));
        entity.modifyComponent(NetIdComp.class, comp -> comp.id = newNetId);

        if (netSyncComp.syncAdd) {
            CreateEntityPacket createEntityPacket = NetEcsUtils.createAddEntityPacket(entity);
            serverModule.sendPacket(createEntityPacket, connectedHosts);
        }
    }

    private void handleEntityRemoved(Entity entity, NetSyncComp netSyncComp, NetworkServerModule serverModule) {
        if (netSyncComp.syncRemove) {
            int netId = entity.getComponent(NetIdComp.class).id;
            RemoveEntityPacket removeEntityPacket = new RemoveEntityPacket(netId);
            serverModule.sendPacket(removeEntityPacket, connectedHosts);
        }
    }

    private void handleHostEntitiesAdded(List<Entity> newHostEntities, NetworkServerModule serverModule) {
        newHostEntities.forEach(newHostEntity -> {
            GameHost newHost = newHostEntity.getComponent(NetHostComp.class).host;

            List<CreateEntityPacket> allEntitiesPackets = entitiesStream()
                    .filter(entity -> entity.getComponent(NetSyncComp.class).syncAdd)
                    .map(entity -> (entity == newHostEntity)
                            ? NetEcsUtils.createAddEntityPacket(entity, Set.of(new ClientControlledComp()))
                            : NetEcsUtils.createAddEntityPacket(entity)
                    )
                    .collect(Collectors.toList());

            allEntitiesPackets
                    .forEach(createEntityPacket -> serverModule.sendPacket(createEntityPacket, newHost));
        });

    }

    private void syncComponents(
            int netId,
            Entity entity,
            Set<Class<? extends Component>> syncComponentTypes,
            NetworkServerModule serverModule
    ) {
        syncComponentTypes.stream()
                .filter(compType -> {
                    if (entity.hasComponent(compType)) {
                        return true;
                    } else {
                        logger.warn("Trying to sync a component that is not present in entity. CompType: " + compType + ", entity: " + entity);
                        return false;
                    }
                })
                .map(entity::getComponent)
                .forEach(comp -> {
                    serverModule.sendPacket(new UpdateComponentPacket(netId, comp), connectedHosts);
                });
    }

    private int createEntityNetId() {
        return nextEntityNetId++;
    }
}
