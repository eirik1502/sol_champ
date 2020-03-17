package sol_engine.network.communication_layer;

import sol_engine.network.packet_handling.NetworkPacket;

import java.util.Arrays;
import java.util.List;

public interface NetworkCommunicationEndpoint {

    void usePacketTypes(List<Class<? extends NetworkPacket>> packetTypes);

    default void usePacketTypes(Class<? extends NetworkPacket>... packetTypes) {
        usePacketTypes(Arrays.asList(packetTypes));
    }
}
