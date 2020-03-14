package sol_engine.network.client;

import sol_engine.network.packet_handling.NetworkEndpoint;
import sol_engine.network.packet_handling.NetworkPacketLayer;
import sol_engine.network.server.Host;

public interface NetworkClient extends NetworkPacketLayer, NetworkEndpoint {

    boolean connect(String address, int port);
}
