package sol_engine.network.network_sol_module;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sol_engine.module.Module;
import sol_engine.network.network_game.PacketsQueue;
import sol_engine.network.network_game.game_client.ClientConnectionData;
import sol_engine.network.network_game.game_client.NetworkGameClient;
import sol_engine.network.packet_handling.NetworkPacket;

import java.util.*;
import java.util.stream.Collectors;

public class NetworkClientModule extends Module {
    private final Logger logger = LoggerFactory.getLogger(NetworkClientModule.class);

    private NetworkClientModuleConfig config;
    private NetworkGameClient client;
    private ClientConnectionData connData = new ClientConnectionData(false);

    private final Map<Class<? extends NetworkPacket>, Deque<NetworkPacket>> currentPacketsOfType = new HashMap<>();


    public NetworkClientModule(NetworkClientModuleConfig config) {
        this.config = config;
    }


    @SafeVarargs
    public final void usePacketTypes(Class<? extends NetworkPacket>... packetTypes) {
        usePacketTypes(Arrays.asList(packetTypes));
    }

    public final void usePacketTypes(List<Class<? extends NetworkPacket>> packetTypes) {
        if (client != null) {
            client.usePacketTypes(packetTypes);
        } else {
            logger.warn("calling usePacketTypes() before client is setup");
        }
    }

    public boolean isConnected() {
        return client != null && client.isConnected();
    }

    public ClientConnectionData getConnectionData() {
        if (connData == null) {
            logger.warn("Getting connection data that is null");
        }
        return connData;
    }

    public void disconnect() {
        if (client != null) {
            client.disconnect();
        } else {
            logger.warn("Calling disconnect before setup");
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends NetworkPacket> Deque<T> peekPacketsOfType(Class<T> type) {
        return new ArrayDeque<>((Collection<T>) currentPacketsOfType.getOrDefault(type, new ArrayDeque<>()));
    }

    public void sendPacket(NetworkPacket packet) {
        client.sendPacket(packet);
    }

    @Override
    public void onSetup() {
        client = new NetworkGameClient();
        client.usePacketTypes(config.packetTypes);
    }

    @Override
    public void onStart() {
        connData = client.connect(config.clientConfig);
        if (!connData.isConnected) {
            throw new IllegalStateException("Client module could not connect to server :(");
        }
    }

    @Override
    public void onEnd() {
        client.disconnect();
    }

    @Override
    public void onUpdate() {
        currentPacketsOfType.clear();
        currentPacketsOfType.putAll(client.pollAllPackets());
    }
}
