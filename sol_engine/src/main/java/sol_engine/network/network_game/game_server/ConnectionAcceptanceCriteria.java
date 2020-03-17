package sol_engine.network.network_game.game_server;

import sol_engine.network.communication_layer.Host;
import sol_engine.network.communication_layer.NetworkServer;

public interface ConnectionAcceptanceCriteria {

    public boolean accepted(NetworkServer server, Host host, String urlPath);
}
