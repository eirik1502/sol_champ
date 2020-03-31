package sol_engine.network.network_game.game_server;

import java.util.ArrayList;
import java.util.List;

public class ServerConnectionData {
    public final String gameId;
    public final String address;
    public final int port;
    public final List<List<String>> teamsPlayersKeys;
    public final boolean allowObservers;
    public final String observerKey;

    public ServerConnectionData() {
        this("-1", "-1", -1, new ArrayList<>(), false, "-1");
    }

    public ServerConnectionData(String gameId, String address, int port, List<List<String>> teamsPlayersKeys, boolean allowObservers, String observerKey) {
        this.gameId = gameId;
        this.address = address;
        this.port = port;
        this.teamsPlayersKeys = teamsPlayersKeys;
        this.allowObservers = allowObservers;
        this.observerKey = observerKey;
    }

    @Override
    public String toString() {
        return "ServerConnectionData{" +
                "gameId='" + gameId + '\'' +
                ", address='" + address + '\'' +
                ", port=" + port +
                ", teamsPlayersKeys=" + teamsPlayersKeys +
                ", allowObservers=" + allowObservers +
                ", observerKey='" + observerKey + '\'' +
                '}';
    }
}
