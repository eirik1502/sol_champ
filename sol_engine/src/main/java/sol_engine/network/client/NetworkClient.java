package sol_engine.network.client;

import sol_engine.network.packet_handling.NetworkRawPacketLayer;

public interface NetworkClient extends NetworkRawPacketLayer {

    boolean connect(String address, int port);

    void terminate();
}
