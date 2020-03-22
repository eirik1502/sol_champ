package sol_engine.network.network_ecs;

import org.joml.Vector2f;
import sol_engine.ecs.Entity;
import sol_engine.ecs.World;
import sol_engine.utils.collections.Pair;

import java.util.List;

public class NetworkEcsUtils {

    public static void createAddNetServerHostSpawner(World world, List<List<Pair<String, Vector2f>>> hostEntityStartData) {
        Entity entity = world.createEntity("default_server_host_spawner")
                .addComponent(new NetServerComp(
                        hostEntityStartData
                ));

        world.addSystem(NetServerSystem.class);
        world.addEntity(entity);
    }
}
