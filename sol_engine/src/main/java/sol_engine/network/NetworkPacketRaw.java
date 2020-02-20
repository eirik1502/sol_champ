package sol_engine.network;

import sol_engine.network.server.Host;

public class NetworkPacketRaw {
    public Host owner;
    public String data;

    public NetworkPacketRaw(Host owner, String data) {
        this.owner = owner;
        this.data = data;
    }
}
