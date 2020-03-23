package sol_engine.network.network_ecs;

import org.joml.Vector2f;

public class EntityHostStartData {
    public String entityClass;
    public Vector2f startPos;


    public EntityHostStartData(String entityClass, Vector2f startPos) {
        this.entityClass = entityClass;
        this.startPos = startPos;
    }
}
