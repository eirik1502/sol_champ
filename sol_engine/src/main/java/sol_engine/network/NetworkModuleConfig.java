package sol_engine.network;

import sol_engine.network.packet_handling.NetworkPacket;
import sol_engine.network.server.ConnectionAcceptanceCriteria;

import java.util.ArrayList;
import java.util.List;

public class NetworkModuleConfig {

    public boolean isServer;
    public int port;
    public String address;
    public List<Class<? extends ConnectionAcceptanceCriteria>> connectionAcceptanceCriteria = new ArrayList<>();
    public List<Class<? extends NetworkPacket>> packetTypes = new ArrayList<>();

    public NetworkModuleConfig() {
    }

    public NetworkModuleConfig(boolean isServer, int port, String address, List<Class<? extends ConnectionAcceptanceCriteria>> connectionAcceptanceCriteria, List<Class<? extends NetworkPacket>> packetTypes) {
        this.isServer = isServer;
        this.port = port;
        this.address = address;
        this.connectionAcceptanceCriteria = connectionAcceptanceCriteria;
        this.packetTypes = packetTypes;
    }
}

