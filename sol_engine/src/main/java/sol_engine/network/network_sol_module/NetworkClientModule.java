package sol_engine.network.network_sol_module;

import sol_engine.module.Module;
import sol_engine.network.network_game.game_client.NetworkGameClient;
import sol_engine.network.packet_handling.NetworkPacket;

import java.util.Deque;

public class NetworkClientModule extends Module {

    private NetworkClientModuleConfig config;
    private NetworkGameClient client;

    public NetworkClientModule(NetworkClientModuleConfig config) {
        this.config = config;
    }

    public boolean isConnected() {
        return client.isConnected();
    }

    public <T extends NetworkPacket> Deque<T> peekPacketsOfType(Class<T> type) {
        return client.peekPacketsOfType(type);
    }

    public void sendPacket(NetworkPacket packet) {
        client.sendPacket(packet);
    }

    @Override
    public void onSetup() {
        client = new NetworkGameClient();
//        client.usePacketTypes(config.packetTypes);
        client.connect(config.clientConfig);
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onEnd() {
        client.disconnect();
    }

    @Override
    public void onUpdate() {

    }
}
