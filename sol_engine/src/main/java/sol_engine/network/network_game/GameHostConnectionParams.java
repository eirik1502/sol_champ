package sol_engine.network.network_game;

import java.util.Map;

public class GameHostConnectionParams {
    public String gameId;
    public String connectionKey;
    public boolean isObserver;
    public String name;

    public GameHostConnectionParams(String gameId, String connectionKey, boolean isObserver, String name) {
        this.gameId = gameId;
        this.name = name;
        this.connectionKey = connectionKey;
        this.isObserver = isObserver;
    }

    public static GameHostConnectionParams fromParmaMap(Map<String, String> paramsMap) {
        return new GameHostConnectionParams(
                paramsMap.getOrDefault("gameId", ""),
                paramsMap.getOrDefault("connectionKey", ""),
                Boolean.parseBoolean(paramsMap.getOrDefault("isObserver", "false")),
                paramsMap.getOrDefault("name", "")
        );
    }

    public Map<String, String> toParamMap() {
        return Map.of(
                "gameId", gameId,
                "connectionKey", connectionKey,
                "isObserver", Boolean.toString(isObserver),
                "name", name
        );
    }


    @Override
    public String toString() {
        return "GameHostConnectionParams{" +
                "gameId='" + gameId + '\'' +
                ", connectionKey='" + connectionKey + '\'' +
                ", isObserver=" + isObserver +
                ", name='" + name + '\'' +
                '}';
    }
}
