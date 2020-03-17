package sol_engine.network.communication_layer;

import sol_engine.network.packet_handling.NetworkPacket;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public interface NetworkCommunicationServer extends NetworkCommunicationEndpoint {

    interface PacketHandler {
        void handlePacket(NetworkPacket packet, Host host);
    }


    void onPacket(PacketHandler handler);

    void sendPacketAll(NetworkPacket packet);

    void sendPacket(NetworkPacket packet, Collection<Host> hosts);

    default void sendPacket(NetworkPacket packet, Host... hosts) {
        sendPacket(packet, Arrays.asList(hosts));
    }
}
