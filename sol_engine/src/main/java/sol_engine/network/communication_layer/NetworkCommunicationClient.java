package sol_engine.network.communication_layer;

import sol_engine.network.packet_handling.NetworkPacket;

import java.util.Arrays;
import java.util.Collection;

public interface NetworkCommunicationClient extends NetworkCommunicationEndpoint {

    interface PacketHandler {
        void handlePacket(NetworkPacket packet);
    }


    void onPacket(PacketHandler handler);

    void sendPacket(NetworkPacket packet);

}
