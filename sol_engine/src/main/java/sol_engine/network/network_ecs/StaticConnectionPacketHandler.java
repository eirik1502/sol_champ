package sol_engine.network.network_ecs;

import sol_engine.ecs.World;
import sol_engine.network.packet_handling.NetworkPacket;

public interface StaticConnectionPacketHandler {
    void handleConnectionPacket(NetworkPacket packet, World wold);
}
