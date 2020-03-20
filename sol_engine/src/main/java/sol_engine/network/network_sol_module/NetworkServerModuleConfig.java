package sol_engine.network.network_sol_module;

import sol_engine.network.packet_handling.NetworkPacket;
import sol_engine.network.network_game.game_server.ServerConfig;

import java.util.ArrayList;
import java.util.List;

public class NetworkServerModuleConfig {

    public ServerConfig serverConfig;
    public List<Class<? extends NetworkPacket>> packetTypes = new ArrayList<>();

    public NetworkServerModuleConfig() {
    }

    public NetworkServerModuleConfig(ServerConfig serverConfig, List<Class<? extends NetworkPacket>> packetTypes) {
        this.serverConfig = serverConfig;
        this.packetTypes = packetTypes;
    }
}

