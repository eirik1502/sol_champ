package sol_engine.network.network_ecs;

import org.joml.Vector2f;
import sol_engine.network.network_game.GameHost;
import sol_engine.network.packet_handling.NetworkPacket;

public class HostConnectedPacket implements NetworkPacket {
    public GameHost host;
    public String entityClass;
    public Vector2f startPos;


    public HostConnectedPacket() {
    }

    public HostConnectedPacket(GameHost host, String entityClass, Vector2f startPos) {
        this.host = host;
        this.entityClass = entityClass;
        this.startPos = startPos;
    }
}
