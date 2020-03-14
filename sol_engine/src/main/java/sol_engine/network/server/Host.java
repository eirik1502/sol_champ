package sol_engine.network.server;

public class Host {
    public String name;
    public String connectionKey;
    public String id;
    public String address;
    public int port;

    public boolean isObserver;
    public int teamIndex;
    public int teamPlayerIndex;


    public Host(String name, String connectionKey, String id, String address, int port, boolean isObserver, int teamIndex, int teamPlayerIndex) {
        this.name = name;
        this.connectionKey = connectionKey;
        this.id = id;
        this.address = address;
        this.port = port;
        this.isObserver = isObserver;
        this.teamIndex = teamIndex;
        this.teamPlayerIndex = teamPlayerIndex;
    }

    @Override
    public String toString() {
        return "Host{" +
                "name='" + name + '\'' +
                ", connectionKey='" + connectionKey + '\'' +
                ", id='" + id + '\'' +
                ", address='" + address + '\'' +
                ", port=" + port +
                ", isObserver=" + isObserver +
                ", teamIndex=" + teamIndex +
                ", teamPlayerIndex=" + teamPlayerIndex +
                '}';
    }
}
