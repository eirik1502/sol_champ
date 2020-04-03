package sol_engine.network.network_ecs.world_syncing;

import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sol_engine.core.ModuleSystemBase;
import sol_engine.ecs.Component;
import sol_engine.ecs.Entity;
import sol_engine.network.network_ecs.host_managing.NetEcsUtils;
import sol_engine.network.network_ecs.host_managing.NetHostComp;
import sol_engine.network.network_ecs.host_managing.NetIdComp;
import sol_engine.network.network_ecs.packets.CreateEntityPacket;
import sol_engine.network.network_ecs.packets.RemoveEntityPacket;
import sol_engine.network.network_ecs.packets.UpdateComponentPacket;
import sol_engine.network.network_sol_module.NetworkServerModule;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class NetSyncServerSystem extends ModuleSystemBase {
    private static Logger logger = LoggerFactory.getLogger(NetSyncServerSystem.class);

    private Set<Entity> prevEntities = new HashSet<>();

    @Override
    protected void onSetup() {
        usingComponents(NetIdComp.class, NetSyncComp.class);
        usingModules(NetworkServerModule.class);
    }

    @Override
    protected void onSetupEnd() {
        getModule(NetworkServerModule.class).usePacketTypes(UpdateComponentPacket.class);
    }

    @Override
    protected void onStart() {
        prevEntities.addAll(entities.copyToList());
    }

    @Override
    protected void onUpdate() {
        NetworkServerModule serverModule = getModule(NetworkServerModule.class);

        // handle added and removed entities
        Set<Entity> currEntities = entities.copyToSet(new HashSet<>());
        if (!currEntities.equals(prevEntities)) {
            Sets.SetView<Entity> removedEntities = Sets.difference(prevEntities, currEntities);
            Sets.SetView<Entity> addedEntities = Sets.difference(currEntities, prevEntities);

            removedEntities.stream()
                    .filter(entity -> entity.getComponent(NetSyncComp.class).syncRemove)
                    .filter(entity -> !entity.hasComponent(NetHostComp.class)) // host entities are handled by the ServerSystem for now
                    .map(entity -> createCreateEntityPacket(
                            entity,
                            entity.getComponent(NetIdComp.class).id,
                            entity.getComponent(NetSyncComp.class).syncComponentTypes
                    ))
                    .forEach(serverModule::sendPacketAll);

            addedEntities.stream()
                    .filter(entity -> entity.getComponent(NetSyncComp.class).syncAdd)
                    .filter(entity -> !entity.hasComponent(NetHostComp.class)) // host entities are handled by the ServerSystem for now
                    .map(entity -> createRemoveEntityPacket(entity.getComponent(NetIdComp.class).id))
                    .forEach(serverModule::sendPacketAll);

            prevEntities = currEntities;
        }

        forEachWithComponents(
                NetIdComp.class,
                NetSyncComp.class,
                (entity, netIdComp, netSyncComp) -> {
                    syncComponents(netIdComp.id, entity, netSyncComp.syncComponentTypes, serverModule);
                });
    }

    private RemoveEntityPacket createRemoveEntityPacket(int netId) {
        return new RemoveEntityPacket(netId);
    }

    private CreateEntityPacket createCreateEntityPacket(
            Entity entity,
            int netId,
            Set<Class<? extends Component>> syncComponents
    ) {
        String className = entity.className;
        if (className == null) {
            logger.error("creating createEntityPacket for an entity with no className");
        }

        Set<Component> updateComponents = NetEcsUtils.componentsToBeSynced(entity, syncComponents, logger);

        return new CreateEntityPacket(
                netId,
                entity.className,
                new ArrayList<>(),
                new ArrayList<>(updateComponents)
        );
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
                    serverModule.sendPacketAll(new UpdateComponentPacket(netId, comp));
                });
    }
}
