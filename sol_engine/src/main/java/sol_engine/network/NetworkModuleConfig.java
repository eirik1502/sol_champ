package sol_engine.network;

import sol_engine.network.client.ClientConfig;
import sol_engine.network.packet_handling.NetworkPacket;
import sol_engine.network.server.ConnectionAcceptanceCriteria;
import sol_engine.network.server.ServerConfig;

import java.util.ArrayList;
import java.util.List;

public class NetworkModuleConfig {

    public boolean isServer;
    public ServerConfig serverConfig = null;  // either server or client config must be present
    public ClientConfig clientConfig = null;  // either server or client config must be present
    public List<Class<? extends NetworkPacket>> packetTypes = new ArrayList<>();

    public NetworkModuleConfig() {
    }

    public NetworkModuleConfig(ServerConfig serverConfig, List<Class<? extends NetworkPacket>> packetTypes) {
        this(true, serverConfig, null, packetTypes);
    }

    public NetworkModuleConfig(ClientConfig clientConfig, List<Class<? extends NetworkPacket>> packetTypes) {
        this(false, null, clientConfig, packetTypes);
    }

    public NetworkModuleConfig(boolean isServer, ServerConfig serverConfig, ClientConfig clientConfig, List<Class<? extends NetworkPacket>> packetTypes) {
        this.isServer = isServer;
        this.serverConfig = serverConfig;
        this.clientConfig = clientConfig;
        this.packetTypes = packetTypes;
    }
}

