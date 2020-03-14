package sol_engine.network.server;

public class ConnectingHost {
    public String address;
    public int port;
    public String gameId;
    public String connectionKey;
    public boolean isObserver;
    public String name;

    public ConnectingHost(String address, int port, String gameId, String connectionKey, boolean isObserver, String name) {
        this.address = address;
        this.port = port;
        this.gameId = gameId;
        this.connectionKey = connectionKey;
        this.isObserver = isObserver;
        this.name = name;
    }

    @Override
    public String toString() {
        return "ConnectingHost{" +
                "address='" + address + '\'' +
                ", port=" + port +
                ", gameId='" + gameId + '\'' +
                ", connectionKey='" + connectionKey + '\'' +
                ", isObserver=" + isObserver +
                ", name='" + name + '\'' +
                '}';
    }
}
