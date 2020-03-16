package sol_engine.network.communication_layer;

import sol_engine.network.packet_handling.NetworkPacket;
import sol_engine.network.server.Host;

import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.List;

public interface NetworkCommunicationLayer {

    interface PacketHandler {
        void handlePacket(NetworkPacket packet, Host host);
    }

    void usePacketTypes(List<Class<? extends NetworkPacket>> packetTypes);

    default void usePacketTypes(Class<? extends NetworkPacket>... packetTypes) {
        usePacketTypes(Arrays.asList(packetTypes));
    }

    void onPacket(PacketHandler handler);

    void sendPacketAll(NetworkPacket packet);

    void sendPacket(NetworkPacket packet, Collection<Host> hosts);

    default void sendPacket(NetworkPacket packet, Host... hosts) {
        sendPacket(packet, Arrays.asList(hosts));
    }
}
