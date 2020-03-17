package sol_engine.network.network_game.game_client;

import sol_engine.network.communication_layer.NetworkClient;
import sol_engine.network.communication_layer.NetworkCommunicationClient;
import sol_engine.network.communication_layer_impls.websockets.NetworkWebsocketsClient;
import sol_engine.network.network_game.GameHostConnectionParams;
import sol_engine.network.packet_handling.NetworkPacket;

import java.util.*;

public class NetworkGameClient
        implements NetworkClient.OpenHandler, NetworkClient.CloseHandler, NetworkCommunicationClient.PacketHandler {
    private static final Deque<NetworkPacket> EMPTY_PACKET_QUEUE = new ArrayDeque<>();

    private NetworkClient client;
    private Map<Class<? extends NetworkPacket>, Deque<NetworkPacket>> pendingPacketsOfType = new HashMap<>();


    public boolean connect(ClientConfig config) {
        GameHostConnectionParams connectParams = new GameHostConnectionParams(
                config.gameId, config.connectionKey, config.isObserver, ""
        );

        client = new NetworkWebsocketsClient();

        client.onOpen(this);
        client.onClose(this);
        client.onPacket(this);

        boolean connected = client.connect(config.address, config.port, connectParams.toParamMap());
        return connected;
    }

    public void disconnect() {
        if (client != null) {
            client.disconnect();  // assumed to call handleClose
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends NetworkPacket> Deque<T> peekPacketsOfType(Class<T> type) {
        return new ArrayDeque<>((Collection<T>) pendingPacketsOfType.getOrDefault(type, new ArrayDeque<>()));
    }

    public <T extends NetworkPacket> Deque<T> pollPacketsOfType(Class<T> type) {
        Deque<T> packets = peekPacketsOfType(type);
        clearPackets();
        return packets;
    }

    public void clearPackets() {
        pendingPacketsOfType.values().forEach(Deque::clear);
    }


    public void sendPacket(NetworkPacket packet) {
        client.sendPacket(packet);
    }

    public boolean isConnected() {
        if (client != null) {
            return client.isConnected();
        } else {
            return false;
        }
    }


    @Override
    public void handleOpen() {
    }

    @Override
    public void handleClose() {
        client = null;
    }

    @Override
    public void handlePacket(NetworkPacket packet) {
        pendingPacketsOfType.putIfAbsent(packet.getClass(), new ArrayDeque<>()).add(packet);
    }
}
