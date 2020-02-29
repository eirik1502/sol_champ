package sol_engine.network.server;

import sol_engine.network.packet_handling.NetworkRawPacketLayer;

public interface NetworkServer extends NetworkRawPacketLayer {

    public void addConnectionAcceptanceCriteria(ConnectionAcceptanceCriteria criteria);

    public void start(int port);

    public void waitForConnections(int count);
}
