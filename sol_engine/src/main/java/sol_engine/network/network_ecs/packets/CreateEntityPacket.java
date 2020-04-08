package sol_engine.network.network_ecs.packets;

import sol_engine.ecs.Component;
import sol_engine.network.packet_handling.NetworkPacket;

import java.util.*;

public class CreateEntityPacket implements NetworkPacket {
    public int netId = -1;
    public String name = "no-name";
    public String entityClass = "-1";
    public HashSet<Component> createComponents = new HashSet<>();
    public HashSet<Component> updateComponents = new HashSet<>();


    public CreateEntityPacket() {
    }

    public CreateEntityPacket(int netId, String name, String entityClass, Set<Component> createComponents, Set<Component> updateComponents) {
        this.netId = netId;
        this.name = name;
        this.entityClass = entityClass;
        this.createComponents = new HashSet<>(createComponents);
        this.updateComponents = new HashSet<>(updateComponents);
    }

    @Override
    public String toString() {
        return "CreateEntityPacket{" +
                "netId=" + netId +
                ", name='" + name + '\'' +
                ", entityClass='" + entityClass + '\'' +
                ", createComponents=" + createComponents +
                ", updateComponents=" + updateComponents +
                '}';
    }
}
