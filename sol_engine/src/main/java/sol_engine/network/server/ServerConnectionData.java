package sol_engine.network.server;

import java.util.List;

public class ServerConnectionData {
    public String gameId;
    public String address;
    public int port;
    public List<List<String>> teamsPlayersKeys;
    public boolean allowObservers;
    public String observerKey;

    public ServerConnectionData() {
    }

    public ServerConnectionData(String gameId, String address, int port, List<List<String>> teamsPlayersKeys, boolean allowObservers, String observerKey) {
        this.gameId = gameId;
        this.address = address;
        this.port = port;
        this.teamsPlayersKeys = teamsPlayersKeys;
        this.allowObservers = allowObservers;
        this.observerKey = observerKey;
    }
}
