package sol_engine.network.network_ecs.host_managing;

import org.joml.Vector2f;
import sol_engine.ecs.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EntityHostStartData {
    public String entityClass;
    public Set<Component> modifyComponents = new HashSet<>();


    public EntityHostStartData(String entityClass) {
        this(entityClass, new HashSet<>());
    }

    public EntityHostStartData(String entityClass, Set<Component> modifyComponents) {
        this.entityClass = entityClass;
        this.modifyComponents = modifyComponents;
    }
}
