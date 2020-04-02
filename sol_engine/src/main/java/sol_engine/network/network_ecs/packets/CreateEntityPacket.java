package sol_engine.network.network_ecs.packets;

import sol_engine.ecs.Component;

import java.util.Collections;
import java.util.List;

public class CreateEntityPacket {
    public int netId = -1;
    public String entityClass = "-1";
    public List<Component> createComponents = Collections.emptyList();
    public List<Component> updateComponents = Collections.emptyList();


    public CreateEntityPacket() {
    }

    public CreateEntityPacket(int netId, String entityClass, List<Component> createComponents, List<Component> updateComponents) {
        this.netId = netId;
        this.entityClass = entityClass;
        this.createComponents = createComponents;
        this.updateComponents = updateComponents;
    }
}
