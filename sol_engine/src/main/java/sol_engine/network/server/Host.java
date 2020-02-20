package sol_engine.network.server;

public class Host {
    public String name;
    public String id;
    public String address;
    public int port;

    public Host(String name, String id, String address, int port) {
        this.name = name;
        this.id = id;
        this.address = address;
        this.port = port;
    }
}
