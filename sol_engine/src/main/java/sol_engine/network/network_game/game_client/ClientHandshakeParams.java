package sol_engine.network.network_game.game_client;

import java.util.Map;

public class ClientHandshakeParams {
    public final int sessionId;
    public final boolean isObserver;
    public final int teamIndex;
    public final int playerIndex;

    public static ClientHandshakeParams fromParams(Map<String, String> params) {
        return new ClientHandshakeParams(
                Integer.parseInt(params.getOrDefault("sessionId", "-1")),
                Boolean.parseBoolean(params.getOrDefault("isObserver", "false")),
                Integer.parseInt(params.getOrDefault("teamIndex", "-1")),
                Integer.parseInt(params.getOrDefault("playerIndex", "-1"))
        );
    }

    public ClientHandshakeParams(int sessionId, boolean isObserver, int teamIndex, int playerIndex) {
        this.sessionId = sessionId;
        this.isObserver = isObserver;
        this.teamIndex = teamIndex;
        this.playerIndex = playerIndex;
    }

    @Override
    public String toString() {
        return "ClientHandshakeParams{" +
                "sessionId=" + sessionId +
                ", isObserver=" + isObserver +
                ", teamIndex=" + teamIndex +
                ", playerIndex=" + playerIndex +
                '}';
    }
}
