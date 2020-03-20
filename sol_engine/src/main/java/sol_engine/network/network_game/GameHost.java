package sol_engine.network.network_game;

public class GameHost {
    // network data
    public String address;
    public int port;

    // set by server
    public int sessionId;

    // client meta data
    public String name;  // client decided name

    // connection data
    public String connectionKey;  // key used to connect

    // host role
    public boolean isObserver;
    public int teamIndex;
    public int teamPlayerIndex;


    public GameHost(String address, int port, int sessionId, String name, String connectionKey, boolean isObserver, int teamIndex, int teamPlayerIndex) {
        this.address = address;
        this.port = port;
        this.sessionId = sessionId;
        this.name = name;
        this.connectionKey = connectionKey;
        this.isObserver = isObserver;
        this.teamIndex = teamIndex;
        this.teamPlayerIndex = teamPlayerIndex;
    }

    @Override
    public String toString() {
        return "GameHost{" +
                "address='" + address + '\'' +
                ", port=" + port +
                ", id='" + sessionId + '\'' +
                ", name='" + name + '\'' +
                ", connectionKey='" + connectionKey + '\'' +
                ", isObserver=" + isObserver +
                ", teamIndex=" + teamIndex +
                ", teamPlayerIndex=" + teamPlayerIndex +
                '}';
    }
}
