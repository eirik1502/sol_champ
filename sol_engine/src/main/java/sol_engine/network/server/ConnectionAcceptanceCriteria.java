package sol_engine.network.server;

import sol_engine.network.communication_layer.NetworkServer;

public interface ConnectionAcceptanceCriteria {

    public boolean accepted(NetworkServer server, Host host, String urlPath);
}
