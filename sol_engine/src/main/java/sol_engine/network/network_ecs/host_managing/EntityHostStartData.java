package sol_engine.network.network_ecs.host_managing;

import org.joml.Vector2f;
import sol_engine.ecs.Component;

import java.util.ArrayList;
import java.util.List;

public class EntityHostStartData {
    public String entityClass;
    public List<Component> modifyComponents = new ArrayList<>();


    public EntityHostStartData(String entityClass) {
        this(entityClass, new ArrayList<>());
    }

    public EntityHostStartData(String entityClass, List<Component> modifyComponents) {
        this.entityClass = entityClass;
        this.modifyComponents = modifyComponents;
    }
}
