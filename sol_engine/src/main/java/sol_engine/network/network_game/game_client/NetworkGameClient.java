package sol_engine.network.network_game.game_client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sol_engine.network.communication_layer.NetworkClient;
import sol_engine.network.communication_layer.NetworkCommunicationClient;
import sol_engine.network.communication_layer_impls.websockets.NetworkWebsocketsClient;
import sol_engine.network.network_game.GameHostConnectionParams;
import sol_engine.network.packet_handling.NetworkPacket;

import java.util.*;

public class NetworkGameClient
        implements NetworkClient.OpenHandler, NetworkClient.CloseHandler, NetworkCommunicationClient.PacketHandler {
    private final Logger logger = LoggerFactory.getLogger(NetworkGameClient.class);


    private NetworkClient client;

    private ClientHandshakeParams onOpenParams = null;

    private Map<Class<? extends NetworkPacket>, Deque<NetworkPacket>> pendingPacketsOfType = new HashMap<>();


    public ClientConnectionData connect(ClientConfig config) {
        GameHostConnectionParams connectParams = new GameHostConnectionParams(
                config.gameId, config.connectionKey, config.isObserver, ""
        );

        client = new NetworkWebsocketsClient();

        client.onOpen(this);
        client.onClose(this);
        client.onPacket(this);

        client.connect(config.address, config.port, connectParams.toParamMap());

        // wait for connection open
        while (onOpenParams == null) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                logger.warn("Interrupted while waiting for onOpen. Will keep waiting. Exception: " + e);
            }
        }

        return new ClientConnectionData(
                isConnected(),
                onOpenParams.sessionId,
                onOpenParams.isObserver,
                onOpenParams.teamIndex,
                onOpenParams.playerIndex
        );
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
    public void handleOpen(Map<String, String> params) {
        onOpenParams = ClientHandshakeParams.fromParams(params);
    }

    @Override
    public void handleClose() {
//        client = null;
    }

    @Override
    public void handlePacket(NetworkPacket packet) {
        pendingPacketsOfType.putIfAbsent(packet.getClass(), new ArrayDeque<>()).add(packet);
    }
}
