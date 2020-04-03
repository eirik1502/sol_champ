package sol_engine.network.network_ecs.packets;

import sol_engine.ecs.Component;
import sol_engine.network.packet_handling.NetworkPacket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CreateEntityPacket implements NetworkPacket {
    public int netId = -1;
    public String entityClass = "-1";
    public ArrayList<Component> createComponents = new ArrayList<>();
    public ArrayList<Component> updateComponents = new ArrayList<>();


    public CreateEntityPacket() {
    }

    public CreateEntityPacket(int netId, String entityClass, ArrayList<Component> createComponents, ArrayList<Component> updateComponents) {
        this.netId = netId;
        this.entityClass = entityClass;
        this.createComponents = createComponents;
        this.updateComponents = updateComponents;
    }

    @Override
    public String toString() {
        return "CreateEntityPacket{" +
                "netId=" + netId +
                ", entityClass='" + entityClass + '\'' +
                ", createComponents=" + createComponents +
                ", updateComponents=" + updateComponents +
                '}';
    }
}
