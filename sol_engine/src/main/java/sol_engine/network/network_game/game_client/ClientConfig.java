package sol_engine.network.network_game.game_client;

public class ClientConfig {
    public String address;
    public int port;
    public String gameId;
    public boolean isObserver;
    public String connectionKey;  // a playerKey or observerKey


    public ClientConfig(String address, int port, String gameId, String playerKey) {
        this(address, port, gameId, playerKey, false);
    }

    public ClientConfig(String address, int port, String gameId, String connectionKey, boolean isObserver) {
        this.address = address;
        this.port = port;
        this.gameId = gameId;
        this.isObserver = isObserver;
        this.connectionKey = connectionKey;
    }
}
