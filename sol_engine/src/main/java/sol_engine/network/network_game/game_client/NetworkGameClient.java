package sol_engine.network.network_game.game_client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sol_engine.network.communication_layer.NetworkClient;
import sol_engine.network.communication_layer.NetworkCommunicationClient;
import sol_engine.network.communication_layer_impls.websockets.NetworkWebsocketsClient;
import sol_engine.network.network_game.GameHostConnectionParams;
import sol_engine.network.packet_handling.NetworkPacket;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;

public class NetworkGameClient
        implements NetworkClient.OpenHandler, NetworkClient.CloseHandler, NetworkCommunicationClient.PacketHandler {
    private final Logger logger = LoggerFactory.getLogger(NetworkGameClient.class);


    private NetworkClient client;

    private ClientConnectionData connectionData = null;  // set when client is opened
    private boolean isObserver;

    private final Map<Class<? extends NetworkPacket>, Deque<NetworkPacket>> pendingPacketsOfType = new HashMap<>();


    public NetworkGameClient() {
        client = new NetworkWebsocketsClient();

        client.onOpen(this);
        client.onClose(this);
        client.onPacket(this);
    }

    public void usePacketTypes(List<Class<? extends NetworkPacket>> packetTypes) {
        if (client != null) {
            client.usePacketTypes(packetTypes);
            logger.info("using packet types: " + packetTypes);
        } else {
            logger.warn("calling usePacketTypes() before client is setup");
        }
    }

    public ClientConnectionData connect(ClientConfig config) {
        GameHostConnectionParams connectParams = new GameHostConnectionParams(
                config.gameId, config.connectionKey, config.isObserver, ""
        );

        logger.info("Connecting to server. Address: " + config.address + ", port: " + config.port + ", with params: " + connectParams);
        boolean connectSuccessful = client.connect(config.address, config.port, connectParams.toParamMap());

        if (!connectSuccessful) {
            logger.warn("Client connection failed");
            client.disconnect();
            connectionData = new ClientConnectionData(false);
        }

        // wait for connection open or close
        while (connectionData == null) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                logger.warn("Interrupted while waiting for onOpen. Will keep waiting. Exception: " + e);
            }
        }

        isObserver = connectionData.isObserver;
        return connectionData;
    }

    public void disconnect() {
        if (client != null) {
            client.disconnect();  // assumed to call handleClose
        }
    }

//    @SuppressWarnings("unchecked")
//    public <T extends NetworkPacket> Deque<T> peekPacketsOfType(Class<T> type) {
//        return new ArrayDeque<>((Collection<T>) pendingPacketsOfType.getOrDefault(type, new ArrayDeque<>()));
//    }
//
//    public <T extends NetworkPacket> Deque<T> pollPacketsOfType(Class<T> type) {
//        // synchronized to not let any packets arrive in between get and clear
//        synchronized (pendingPacketsOfType) {
//            Deque<T> packets = peekPacketsOfType(type);
//            pendingPacketsOfType.getOrDefault(type, new ArrayDeque<>()).clear();
//            return packets;
//        }
//    }

    public Map<Class<? extends NetworkPacket>, Deque<NetworkPacket>> pollAllPackets() {
        synchronized (pendingPacketsOfType) {
            Map<Class<? extends NetworkPacket>, Deque<NetworkPacket>> allPackets = pendingPacketsOfType.entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> new ArrayDeque<>(entry.getValue())
                    ));
            clearAllPackets();
            return allPackets;
        }
    }

    public void clearAllPackets() {
        pendingPacketsOfType.values().forEach(Deque::clear);
    }


    public void sendPacket(NetworkPacket packet) {
        if (!isObserver) {
            logger.debug("Sending packet, type: " + packet.getClass().getSimpleName() + " data: " + packet);
            client.sendPacket(packet);
        } else {
            logger.warn("An observer can not send packets");
        }
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
        ClientHandshakeParams onOpenParams = ClientHandshakeParams.fromParams(params);
        connectionData = new ClientConnectionData(
                true,
                onOpenParams.sessionId,
                onOpenParams.isObserver,
                onOpenParams.teamIndex,
                onOpenParams.playerIndex
        );
    }

    @Override
    public void handleClose() {
        // handle if the client was never connected
        if (connectionData == null) {
            connectionData = new ClientConnectionData(false);
            logger.warn("Connection closed before opened");
        }
//        client = null;
    }

    @Override
    public void handlePacket(NetworkPacket packet) {
        logger.debug("Received packet, type: " + packet.getClass().getSimpleName() + " data: " + packet);

        // synchronizing to not interfere with the poll method
        synchronized (pendingPacketsOfType) {
            pendingPacketsOfType.computeIfAbsent(packet.getClass(), key -> new ConcurrentLinkedDeque<>()).add(packet);
        }
    }
}
