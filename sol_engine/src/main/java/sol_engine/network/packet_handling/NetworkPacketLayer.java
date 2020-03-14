package sol_engine.network.packet_handling;

import sol_engine.network.server.Host;

import java.util.Arrays;
import java.util.Deque;
import java.util.List;

public interface NetworkPacketLayer {

    interface PacketHandler {
        void handlePacket(NetworkPacket packet);
    }

    void onPacket(PacketHandler handler);

    void sendPacketAll(NetworkPacket packet);

    void sendPacket(NetworkPacket packet, List<Host> hosts);

    default void sendPacket(NetworkPacket packet, Host... hosts) {
        sendPacket(packet, Arrays.asList(hosts));
    }
}
