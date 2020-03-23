package sol_engine.network.network_ecs;

import org.joml.Vector2f;
import sol_engine.core.TransformComp;
import sol_engine.ecs.Entity;
import sol_engine.ecs.World;
import sol_engine.input_module.InputComp;
import sol_engine.network.network_game.GameHost;
import sol_engine.utils.collections.Pair;

import java.util.List;

public class NetEcsUtils {

    public static void addNetServerHostSpawner(World world, List<List<EntityHostStartData>> hostEntityStartData) {
        Entity entity = world.createEntity("default_server_host_spawner")
                .addComponent(new NetServerComp(
                        hostEntityStartData
                ));

        world.addSystem(NetServerSystem.class);
        world.addEntity(entity);
    }

    public static void addNetClientHostSpawner(World world) {
        Entity entity = world.createEntity("default_client_host_spawner")
                .addComponent(new NetClientComp());

        world.addSystem(NetClientSystem.class);
        world.addEntity(entity);
    }

    static Entity addEntityForHost(
            boolean isServer,
            GameHost host,
            String entityClass,
            Vector2f startPos,
            World world
    ) {
        String name = entityClass + "_" + host.name;
        Entity hostEntity = world.instanciateEntityClass(entityClass, name)
                .addComponent(new NetIdComp(host.sessionId))
                .modifyIfHasComponent(TransformComp.class, comp -> comp.position.set(startPos));

        if (isServer) {
            String inputGroup = "t" + host.teamIndex + "p" + host.playerIndex;
            hostEntity.modifyIfHasComponent(InputComp.class, comp -> comp.inputGroup = inputGroup);
        }
        return hostEntity;
    }
}
