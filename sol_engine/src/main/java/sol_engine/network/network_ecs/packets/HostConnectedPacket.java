package sol_engine.network.network_ecs.packets;

import org.joml.Vector2f;
import sol_engine.network.network_game.GameHost;
import sol_engine.network.packet_handling.NetworkPacket;

public class HostConnectedPacket implements NetworkPacket {
    public GameHost host = null;
    public String entityClass = "";
    public Vector2f startPos = new Vector2f();


    public HostConnectedPacket() {
    }

    public HostConnectedPacket(GameHost host, String entityClass, Vector2f startPos) {
        this.host = host;
        this.entityClass = entityClass;
        this.startPos = startPos;
    }
}
