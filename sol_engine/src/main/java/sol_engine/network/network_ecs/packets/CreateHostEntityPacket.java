package sol_engine.network.network_ecs.packets;

import sol_engine.ecs.Component;
import sol_engine.network.network_game.GameHost;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class CreateHostEntityPacket extends CreateEntityPacket {

    public GameHost host;


    public CreateHostEntityPacket() {
        super();
    }

    public CreateHostEntityPacket(GameHost host, String name, int netId, String entityClass) {
        this(host, netId, name, entityClass, Collections.emptySet(), Collections.emptySet());
    }

    public CreateHostEntityPacket(GameHost host, int netId, String name, String entityClass, Set<Component> createComponents, Set<Component> updateComponents) {
        super(netId, name, entityClass, createComponents, updateComponents);
        this.host = host;
    }

    @Override
    public String toString() {
        return "CreateHostEntityPacket{" +
                "host=" + host +
                ", netId=" + netId +
                ", entityClass='" + entityClass + '\'' +
                ", createComponents=" + createComponents +
                ", updateComponents=" + updateComponents +
                '}';
    }
}
