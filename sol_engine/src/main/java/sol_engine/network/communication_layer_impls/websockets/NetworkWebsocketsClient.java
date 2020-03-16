package sol_engine.network.communication_layer_impls.websockets;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sol_engine.network.communication_layer.NetworkClient;
import sol_engine.network.communication_layer.PacketClassStringConverter;
import sol_engine.network.network_utils.NetworkUtils;
import sol_engine.network.packet_handling.NetworkPacket;
import sol_engine.network.server.Host;

import java.net.URI;
import java.util.*;
import java.util.concurrent.TimeUnit;


public class NetworkWebsocketsClient implements NetworkClient {
    private final Logger logger = LoggerFactory.getLogger(NetworkWebsocketsClient.class);

    private OpenHandler openHandler;
    private CloseHandler closeHandler;
    private PacketHandler packetHandler;

    private final PacketClassStringConverter packetConverter = new PacketClassStringConverter();

    private WebSocketClient wsClient;


    @Override
    public void usePacketTypes(List<Class<? extends NetworkPacket>> packetTypes) {
        packetConverter.usePacketTypes(packetTypes);
    }

    @Override
    public boolean connect(String address, int port, Map<String, String> connectParams) {
        URI uri = NetworkUtils.websocketsURI(address, port, connectParams);
        if (uri == null) {
            logger.error("websockets uri invalid, address: {}, port: {}, params: {}", address, port, connectParams);
            return false;
        }

        wsClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                openHandler.handleOpen();
                logger.info("Client connected to server at address: " + wsClient.getRemoteSocketAddress());
            }

            @Override
            public void onMessage(String message) {
                NetworkPacket packet = packetConverter.stringToPacket(message);
                if (packet != null) {
                    packetHandler.handlePacket(packet, null);
                } else {
                    logger.warn("Received packet that could not be converted from string");
                }
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                closeHandler.handleClose();
                logger.info("Connection closed, status: " + code + " because: " + reason);
            }

            @Override
            public void onError(Exception ex) {
                logger.info("An error occured in WebsocketsClient: " + ex);
            }
        };

        try {
            logger.info("Trying to connect to " + uri);
            wsClient.connectBlocking(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.error("Client interrupted while connecting to server: " + e);
        }
        return isConnected();
    }

    @Override
    public void disconnect() {
        if (wsClient != null) {
            wsClient.close();
        }
    }

    @Override
    public void terminate() {
        if (wsClient != null) {
            wsClient.close();
        }
    }

    @Override
    public boolean isConnected() {
        return wsClient != null && wsClient.isOpen();
    }

    @Override
    public void sendPacketAll(NetworkPacket packet) {
        if (isConnected()) {
            String packetString = packetConverter.packetToString(packet);

            if (packetString != null) {
                wsClient.send(packetString);

                logger.info("Packet sendt: " + packetString);
            } else {
                logger.warn("Packet could not be converted to string: " + packet);
            }
        } else {
            logger.warn("Sending packet while not connected to server: " + packet);
        }
    }

    @Override
    public void sendPacket(NetworkPacket packet, Collection<Host> hosts) {
        sendPacketAll(packet);
    }

    // --- assign handlers ---

    @Override
    public void onOpen(OpenHandler handler) {
        openHandler = handler;
    }

    @Override
    public void onClose(CloseHandler handler) {
        closeHandler = handler;
    }

    @Override
    public void onPacket(PacketHandler handler) {
        packetHandler = handler;
    }
}
