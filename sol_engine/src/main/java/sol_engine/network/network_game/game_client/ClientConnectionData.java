package sol_engine.network.network_game.game_client;

import java.util.Map;

public class ClientConnectionData {
    public final boolean isConnected;  // if connection was successful
    public final int sessionId;
    public final boolean isObserver;  // the actual value of isObserver provided by the server
    public final int teamIndex;  // the actual team index given by the server, usually equal to the one requested
    public final int playerIndex;  // the actual player index given by the server, usually equal to the one requested

    public ClientConnectionData() {
        this(false, -1, false, -1, -1);
    }

    public ClientConnectionData(boolean isConnected, int sessionId, boolean isObserver, int teamIndex, int playerIndex) {
        this.isConnected = isConnected;
        this.sessionId = sessionId;
        this.isObserver = isObserver;
        this.teamIndex = teamIndex;
        this.playerIndex = playerIndex;
    }

    @Override
    public String toString() {
        return "ClientConnectionData{" +
                "isConnected=" + isConnected +
                ", sessionId='" + sessionId + '\'' +
                ", isObserver=" + isObserver +
                ", teamIndex=" + teamIndex +
                ", playerIndex=" + playerIndex +
                '}';
    }
}
