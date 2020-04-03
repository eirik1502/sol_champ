package sol_engine.network.network_ecs.host_managing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sol_engine.ecs.Component;
import sol_engine.ecs.Entity;
import sol_engine.ecs.World;
import sol_engine.input_module.InputComp;
import sol_engine.network.network_game.GameHost;
import sol_engine.network.packet_handling.NetworkPacket;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class NetEcsUtils {
    private static Logger logger = LoggerFactory.getLogger(NetEcsUtils.class);

    public static void addNetServerHostSpawner(
            World world,
            NetworkPacket staticConnectionPacket,
            List<List<EntityHostStartData>> hostEntityStartData
    ) {
        Entity entity = world.createEntity("default_server_host_spawner")
                .addComponent(new NetServerComp(
                        staticConnectionPacket,
                        hostEntityStartData
                ));

        world.addSystem(NetServerSystem.class);
        world.addEntity(entity);
    }

    public static void addNetClientHostSpawner(
            World world,
            Class<? extends NetworkPacket> staticConnectPacketType,
            Class<? extends StaticConnectionPacketHandler> staticConnectionPacketHandler
    ) {
        Entity entity = world.createEntity("default_client_host_spawner")
                .addComponent(new NetClientComp(
                        staticConnectPacketType,
                        staticConnectionPacketHandler
                ));

        world.addSystem(NetClientSystem.class);
        world.addEntity(entity);
    }

    static Entity addEntityForHost(
            boolean isServer,
            GameHost host,
            int netId,
            String entityClass,
            List<Component> modifyComponents,
            World world
    ) {
        String name = entityClass + "_" + host.name;
        Entity hostEntity = world.addEntity(name, entityClass)
                .addComponent(new NetHostComp(host))
                .addComponent(new NetIdComp(netId));
//                .modifyIfHasComponent(TransformComp.class, comp -> comp.position.set(startPos));
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

    public static Set<Component> componentsToBeSynced(Entity entity, Set<Class<? extends Component>> syncComponentTypes) {
        return componentsToBeSynced(entity, syncComponentTypes, logger);
    }

    public static Set<Component> componentsToBeSynced(Entity entity, Set<Class<? extends Component>> syncComponentTypes, Logger _logger) {
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
}
