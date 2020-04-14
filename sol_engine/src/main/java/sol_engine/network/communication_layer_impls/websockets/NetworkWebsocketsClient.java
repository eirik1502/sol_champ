package sol_engine.network.communication_layer_impls.websockets;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.java_websocket.framing.CloseFrame;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sol_engine.network.communication_layer.NetworkClient;
import sol_engine.network.communication_layer.PacketClassStringConverter;
import sol_engine.network.network_utils.NetworkUtils;
import sol_engine.network.packet_handling.NetworkPacket;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class NetworkWebsocketsClient implements NetworkClient {
    private final Logger logger = LoggerFactory.getLogger(NetworkWebsocketsClient.class);

    private OpenHandler openHandler = (params) -> {
    };
    private CloseHandler closeHandler = () -> {
    };
    private PacketHandler packetHandler = (packet) -> {
    };

    private final PacketClassStringConverter packetConverter = new PacketClassStringConverter();

    private WebSocketClient wsClient;


    @Override
    public void usePacketTypes(List<Class<? extends NetworkPacket>> packetTypes) {
        packetConverter.usePacketTypes(packetTypes);
    }

    public boolean connect(String address, int port) {
        return connect(address, port, new HashMap<>());
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
                Map<String, String> paramsFromServer = new HashMap<>();

                if (handshakedata.hasFieldValue(NetworkWebsocketsConsts.HANDSHAKE_EXISTING_FIELDS_FIELD_NAME)) {
                    String customFieldNamesStr = handshakedata.getFieldValue(NetworkWebsocketsConsts.HANDSHAKE_EXISTING_FIELDS_FIELD_NAME);
                    // field names should be comma-separated
                    String[] customFiledNames = customFieldNamesStr.split(",");
                    Arrays.stream(customFiledNames)
                            .map(String::strip)  // clean fields in case they have spaces
                            .forEach(field -> paramsFromServer.put(field, handshakedata.getFieldValue(field)));
                }

                openHandler.handleOpen(paramsFromServer);
                logger.info("Client connected to server at address: " + wsClient.getRemoteSocketAddress());
            }

            @Override
            public void onMessage(String message) {
                NetworkPacket packet = packetConverter.stringToPacket(message);
                if (packet != null) {
                    packetHandler.handlePacket(packet);
                } else {
                    logger.error("Received packet that could not be converted from string: " + message);
                }
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                closeHandler.handleClose();
                logger.info("Connection closed, status: " + code + " because: " + reason);
            }

            @Override
            public void onError(Exception ex) {
                logger.warn("An error occured in WebsocketsClient: " + ex);
                ex.printStackTrace();
            }
        };

        boolean connected = false;
        try {
            logger.info("Trying to connect to " + uri);
            connected = wsClient.connectBlocking(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.error("Client interrupted while connecting to server: " + e);
        }
        return connected;
    }

    @Override
    public void disconnect() {
        if (wsClient != null) {
            wsClient.close(CloseFrame.NORMAL, "called disconnect()");
        }
    }

    @Override
    public boolean isConnected() {
        return wsClient != null && wsClient.isOpen();
    }

    @Override
    public int getLocalPort() {
        if (wsClient != null) {
            return wsClient.getLocalSocketAddress().getPort();
        } else {
            logger.warn("Cannot get local port when client is not connected");
            return -1;
        }
    }

    @Override
    public void sendPacket(NetworkPacket packet) {
        try {
            String packetString = packetConverter.packetToString(packet);

            if (packetString != null) {
                wsClient.send(packetString);

                logger.debug("Packet sendt: " + packetString);
            } else {
                logger.error("Packet could not be converted to string. Packet type: " + packet.getClass().getSimpleName() + ", packet: " + packet);
            }
        } catch (WebsocketNotConnectedException e) {
            logger.debug("Sending packet while not connected to server: " + packet);
        }
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
