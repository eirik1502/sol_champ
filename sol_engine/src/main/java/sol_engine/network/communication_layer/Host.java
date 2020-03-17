package sol_engine.network.communication_layer;

/**
 * This class represents a remote host that is connected
 */
public class Host {
    public String address;
    public int port;


    public Host(String address, int port) {
        this.address = address;
        this.port = port;
    }

    @Override
    public String toString() {
        return "Host{" +
                "address='" + address + '\'' +
                ", port=" + port +
                '}';
    }
}
