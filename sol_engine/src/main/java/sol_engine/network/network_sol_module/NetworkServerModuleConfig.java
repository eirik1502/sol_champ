package sol_engine.network.network_sol_module;

import sol_engine.network.packet_handling.NetworkPacket;
import sol_engine.network.network_game.game_server.GameServerConfig;

import java.util.ArrayList;
import java.util.List;

public class NetworkServerModuleConfig {

    public GameServerConfig gameServerConfig;
    public List<Class<? extends NetworkPacket>> packetTypes = new ArrayList<>();
    public boolean waitForAllPlayerConnections = false;

    public NetworkServerModuleConfig() {
    }

    public NetworkServerModuleConfig(GameServerConfig gameServerConfig, List<Class<? extends NetworkPacket>> packetTypes, boolean waitForAllPlayerConnections) {
        this.gameServerConfig = gameServerConfig;
        this.packetTypes = packetTypes;
        this.waitForAllPlayerConnections = waitForAllPlayerConnections;
    }
}

