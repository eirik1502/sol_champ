package sol_engine.network.network_ecs.packets;

import sol_engine.ecs.Component;
import sol_engine.network.network_game.GameHost;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CreateHostEntityPacket extends CreateEntityPacket {

    public GameHost host;


    public CreateHostEntityPacket() {
        super();
    }

    public CreateHostEntityPacket(GameHost host, int netId, String entityClass) {
        this(host, netId, entityClass, Collections.emptyList(), Collections.emptyList());
    }

    public CreateHostEntityPacket(GameHost host, int netId, String entityClass, List<Component> createComponents, List<Component> updateComponents) {
        super(netId, entityClass, new ArrayList<>(createComponents), new ArrayList<>(updateComponents));
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
