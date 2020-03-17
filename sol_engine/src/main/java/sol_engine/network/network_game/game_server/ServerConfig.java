package sol_engine.network.network_game.game_server;

import java.util.ArrayList;
import java.util.List;

public class ServerConfig {
    public List<Integer> teamSizes = new ArrayList<>();
    public boolean allowObservers;
    public int port = -1;  // A free port will be chosen if the value is -1
    boolean acceptAllConnections = false;  // bypass playerKeys and observerKeys. For testing purposes

    public ServerConfig(List<Integer> teamSizes, boolean allowObservers) {
        this(-1, teamSizes, allowObservers);
    }

    public ServerConfig(int port, List<Integer> teamSizes, boolean allowObservers) {
        this(port, teamSizes, allowObservers, false);
    }

    public ServerConfig(int port, List<Integer> teamSizes, boolean allowObservers, boolean acceptAllConnections) {
        this.port = port;
        this.teamSizes = teamSizes;
        this.allowObservers = allowObservers;
        this.acceptAllConnections = acceptAllConnections;
    }
}
