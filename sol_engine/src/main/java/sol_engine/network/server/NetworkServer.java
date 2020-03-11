package sol_engine.network.server;

import sol_engine.network.packet_handling.NetworkRawPacketLayer;

public interface NetworkServer extends NetworkRawPacketLayer {

    public void addConnectionAcceptanceCriteria(ConnectionAcceptanceCriteria criteria);

    public ConnectionData start();

    public void waitForConnections();
}
