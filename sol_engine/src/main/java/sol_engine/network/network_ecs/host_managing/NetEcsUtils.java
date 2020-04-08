package sol_engine.network.network_ecs.host_managing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sol_engine.ecs.Component;
import sol_engine.ecs.Entity;
import sol_engine.ecs.World;
import sol_engine.input_module.InputComp;
import sol_engine.network.network_ecs.packets.CreateEntityPacket;
import sol_engine.network.network_ecs.packets.UpdateComponentPacket;
import sol_engine.network.network_ecs.world_syncing.NetSyncClientSystem;
import sol_engine.network.network_ecs.world_syncing.NetSyncComp;
import sol_engine.network.network_ecs.world_syncing.NetSyncServerSystem;
import sol_engine.network.network_game.GameHost;
import sol_engine.network.packet_handling.NetworkPacket;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class NetEcsUtils {
    private static Logger logger = LoggerFactory.getLogger(NetEcsUtils.class);

    public static Entity addNetServerEntity(
            World world,
            NetworkPacket staticConnectionPacket,
            List<List<EntityHostStartData>> hostEntityStartData
    ) {
        Entity entity = world.createEntity("server-entity")
                .addComponent(new NetServerComp(
                        staticConnectionPacket,
                        hostEntityStartData
                ));

        return world.addEntity(entity);
    }

    public static Entity addNetClientEntity(
            World world,
            Class<? extends NetworkPacket> staticConnectPacketType,
            Class<? extends StaticConnectionPacketHandler> staticConnectionPacketHandler
    ) {
        Entity entity = world.createEntity("client-entity")
                .addComponent(new NetClientComp(
                        staticConnectPacketType,
                        staticConnectionPacketHandler
                ));

        return world.addEntity(entity);
    }

    static Entity addEntityForHost(
            boolean isServer,
            GameHost host,
            String entityClass,
            Set<Component> modifyComponents,
            Set<Component> createComponents,
            World world
    ) {
        String name = entityClass + "_" + host.name;
        Entity hostEntity = world.addEntity(name, entityClass)
                .addComponent(new NetHostComp(host));
        createComponents.forEach(comp -> hostEntity.addComponent(comp.clone()));
        modifyComponents.forEach(targetComp ->
                hostEntity.modifyIfHasComponent(targetComp.getClass(), comp -> comp.copy(targetComp))
        );

        if (isServer) {
            String inputGroup = "t" + host.teamIndex + "p" + host.playerIndex;
            hostEntity.modifyIfHasComponent(InputComp.class, comp -> comp.inputGroup = inputGroup);
        }
        return hostEntity;
    }

    public static Optional<Entity> getEntityWithNetId(int entityNetId, World world) {
        return world.insight.getEntities().stream()
                .filter(entity -> entity.hasComponent(NetIdComp.class))
                .filter(entity -> entity.getComponent(NetIdComp.class).id == entityNetId)
                .findFirst();
    }

    public static Set<Component> getComponentsOfTypes(Entity entity, Set<Class<? extends Component>> syncComponentTypes) {
        return getComponentsOfTypes(entity, syncComponentTypes, logger);
    }

    public static Set<Component> getComponentsOfTypes(Entity entity, Set<Class<? extends Component>> syncComponentTypes, Logger _logger) {
        return syncComponentTypes.stream()
                .filter(compType -> {
                    if (entity.hasComponent(compType)) {
                        return true;
                    } else {
                        _logger.warn("Trying to sync a component that is not present in entity. CompType: " + compType + ", Entity: " + entity);
                        return false;
                    }
                })
                .map(entity::getComponent)
                .collect(Collectors.toSet());
    }

    public static CreateEntityPacket createAddEntityPacket(Entity entity) {
        String className = entity.className;
        if (className == null) {
            logger.warn("cannot sync entities that are not created from an EtityClass for an entity with no className");
            return null;
        }

        if (!entity.hasComponents(Set.of(NetIdComp.class, NetSyncComp.class))) {
            logger.warn("Trying to create AddEntityPacket of an entity without a NetSyncComp and/or a NetIdComp." +
                    " Returning null. For Entity: " + entity);
            return null;
        }
        NetSyncComp netSyncComp = entity.getComponent(NetSyncComp.class);
        if (!netSyncComp.syncAdd) {
            logger.warn("Trying to create AddEntityPacket of an entity with syncAdd set to false. Returning null. Ffor entity: " + entity);
            return null;
        }

        NetIdComp netIdComp = entity.getComponent(NetIdComp.class);

        Set<Component> updateComponents = NetEcsUtils.getComponentsOfTypes(entity, netSyncComp.syncComponentTypes, logger);
        Set<Component> createComponents = NetEcsUtils.getComponentsOfTypes(entity, netSyncComp.createComponentTypesOnAdd, logger);

        return new CreateEntityPacket(
                netIdComp.id,
                entity.name,
                className,
                createComponents,
                updateComponents
        );
    }

    public static Entity addEntityFromPacket(CreateEntityPacket createEntityPacket, World world) {
        Entity entity = world.addEntity(createEntityPacket.name, createEntityPacket.entityClass);
        entity.addComponent(new NetIdComp(createEntityPacket.netId));
        createEntityPacket.updateComponents.forEach(targetComp ->
                entity.modifyIfHasComponent(targetComp.getClass(), comp -> comp.copy(targetComp))
        );
        createEntityPacket.createComponents.forEach(entity::addComponent);
        return entity;
    }

    public static Entity updateComponentsFromPacket(UpdateComponentPacket packet, Entity entity) {
        if (!entity.hasComponent(NetSyncComp.class)) {
            logger.warn("Trying to update a component of an entity that does not have a NetSyncComp. Returning null. For Entity: " + entity);
            return null;
        }

        Component targetComp = packet.component;
        Class<? extends Component> compType = targetComp.getClass();

        NetSyncComp netSyncComp = entity.getComponent(NetSyncComp.class);
        if (!netSyncComp.syncComponentTypes.contains(compType)) {
            logger.warn("Trying to update a component that is not registered to be synced in the NetSyncComp." +
                    " Returning null. For Entity: " + entity + " , for CompType: " + compType);
            return null;
        }
        return entity.modifyIfHasComponent(compType, comp -> comp.copy(targetComp));
    }
}
