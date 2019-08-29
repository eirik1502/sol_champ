package sol_engine.network_module.connection_handler;

import sol_engine.network_module.NetPacket;

import java.util.ArrayList;
import java.util.List;

public class ConnectedHost {

    public final String address;
    public final String name;

    public ConnectedHost(String address, String name) {
        this.address = address;
        this.name = name;
    }

    public void send(NetPacket packet) {

    }

    public List<NetPacket> pollPackets() {
        return new ArrayList<>();
    }
}
