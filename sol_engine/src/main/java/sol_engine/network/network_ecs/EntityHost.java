package sol_engine.network.network_ecs;

import sol_engine.ecs.Entity;
import sol_engine.network.network_game.GameHost;

public class EntityHost {
    public Entity entity;
    public GameHost host;
    public String entityClass;


    public EntityHost(Entity entity, GameHost host, String entityClass) {
        this.entity = entity;
        this.host = host;
        this.entityClass = entityClass;
    }
}
