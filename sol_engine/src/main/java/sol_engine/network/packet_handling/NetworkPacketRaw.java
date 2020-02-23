package sol_engine.network.packet_handling;

import sol_engine.network.server.Host;

public class NetworkPacketRaw {
    public Host owner;
    public String data;

    public NetworkPacketRaw(Host owner, String data) {
        this.owner = owner;
        this.data = data;
    }

    public String toString() {
        return "[NetworkPacketRaw owner: " + owner + ", data: " + data + "]";
    }
}
