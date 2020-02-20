package sol_engine.network.client;

import sol_engine.network.NetworkRawPacketLayer;

public interface NetworkClient extends NetworkRawPacketLayer {

    boolean connect(String address, int port);
}
