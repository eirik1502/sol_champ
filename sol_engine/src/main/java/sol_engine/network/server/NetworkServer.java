package sol_engine.network.server;

import sol_engine.network.packet_handling.NetworkRawPacketLayer;

public interface NetworkServer extends NetworkRawPacketLayer {

    public ServerConnectionData start(ServerConfig config);

    public void waitForConnections();
}
