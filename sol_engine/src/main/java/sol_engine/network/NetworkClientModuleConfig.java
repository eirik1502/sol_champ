package sol_engine.network;

import sol_engine.network.network_game.game_client.ClientConfig;
import sol_engine.network.packet_handling.NetworkPacket;

import java.util.ArrayList;
import java.util.List;

public class NetworkClientModuleConfig {

    public ClientConfig clientConfig;
    public List<Class<? extends NetworkPacket>> packetTypes = new ArrayList<>();

    public NetworkClientModuleConfig(ClientConfig clientConfig, List<Class<? extends NetworkPacket>> packetTypes) {
        this.clientConfig = clientConfig;
        this.packetTypes = packetTypes;
    }
}
