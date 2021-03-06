package sol_engine.network.communication_layer_impls.websockets;

import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.exceptions.InvalidDataException;
import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.java_websocket.framing.CloseFrame;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.ServerHandshakeBuilder;
import org.java_websocket.server.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sol_engine.network.communication_layer.Host;
import sol_engine.network.communication_layer.NetworkServer;
import sol_engine.network.communication_layer.PacketClassStringConverter;
import sol_engine.network.network_utils.NetworkUtils;
import sol_engine.network.packet_handling.NetworkPacket;
import sol_engine.network.network_game.game_server.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.stream.Collectors;

public class NetworkWebsocketsServer implements NetworkServer {
    private final Logger logger = LoggerFactory.getLogger(NetworkWebsocketsServer.class);

    private HandshakeHandler handshakeHander = (host, params) -> new HandshakeResponse(true, null);
    private OpenHandler openHandler = (host) -> true;
    private CloseHandler closeHandler = (host) -> true;
    private PacketHandler packetHandler = (packet, host) -> {
    };

    private HashMap<WebSocket, Host> socketToHost = new HashMap<>();
    private HashMap<Host, WebSocket> hostToSocket = new HashMap<>();

    private WebSocketServer wsServer;
    private PacketClassStringConverter packetConverter = new PacketClassStringConverter();

    @Override
    public void usePacketTypes(List<Class<? extends NetworkPacket>> packetTypes) {
        packetConverter.usePacketTypes(packetTypes);
    }

    @Override
    public void start(int port) {
        wsServer = createWsServer(port);
        wsServer.start();
    }

    @Override
    public void disconnectHost(Host host) {
        WebSocket sock = hostToSocket.get(host);
        sock.close();  // sock will be run through onClose
    }

    @Override
    public boolean isRunning() {
        return wsServer != null;
    }

    @Override
    public Set<Host> getConnectedHosts() {
        return new HashSet<>(hostToSocket.keySet());
    }


    private WebSocketServer createWsServer(int port) {
        return new WebSocketServer(new InetSocketAddress(port)) {
            @Override
            public void onOpen(WebSocket conn, ClientHandshake handshake) {
                Host host = conn.getAttachment();

                socketToHost.put(conn, host);
                hostToSocket.put(host, conn);

                boolean hostValid = openHandler.handleOpen(host);

                if (!hostValid) {
                    disconnectHost(host);
                    logger.warn("Connection invalid when opening, after handshake, will disconnect. For host: " + host);
                } else {
                    logger.info("Connection opened for host: " + host);
                }
            }

            @Override
            public synchronized void onClose(WebSocket conn, int code, String reason, boolean remote) {
                if (socketToHost.containsKey(conn)) {
                    Host host = socketToHost.get(conn);

                    closeHandler.handleClose(host);

                    socketToHost.remove(conn);
                    hostToSocket.remove(host);

                    logger.info("Client disconnected: " + host);
                } else {
                    logger.warn("Disconnecting host was never connected");
                }
            }

            @Override
            public void onMessage(WebSocket sock, String message) {
                if (socketToHost.containsKey(sock)) {
                    Host fromHost = socketToHost.get(sock);
                    NetworkPacket packet = packetConverter.stringToPacket(message);

                    if (packet != null) {
                        packetHandler.handlePacket(packet, fromHost);
                    } else {
                        logger.error("Could not handle packet as it could not be converted from string");
                    }
                } else {
                    logger.warn("Got message from a host that is not connected");
                }
            }

            @Override
            public void onError(WebSocket sock, Exception ex) {
                Host forHost = socketToHost.get(sock);
                logger.warn("An error occurred for host: " + forHost + ", error: " + ex);
                ex.printStackTrace();
            }

            @Override
            public void onStart() {
                logger.info("Websockets server listening at port: " + this.getPort());
            }


            @Override
            public ServerHandshakeBuilder onWebsocketHandshakeReceivedAsServer(
                    WebSocket conn,
                    Draft draft,
                    ClientHandshake request
            ) throws InvalidDataException {
                ServerHandshakeBuilder builder = super.onWebsocketHandshakeReceivedAsServer(conn, draft, request);

                Map<String, String> queryParams = NetworkUtils.parseQueryParams(conn.getResourceDescriptor());
                Host host = new Host(
                        conn.getRemoteSocketAddress().getHostName(),
                        conn.getRemoteSocketAddress().getPort()
                );

                HandshakeResponse handlerResponse = handshakeHander.handleHandshake(host, queryParams);

                if (!handlerResponse.accepted) {
                    throw new InvalidDataException(CloseFrame.POLICY_VALIDATION, "host not accepted");
                }

                if (handlerResponse.params != null) {
                    builder.put(NetworkWebsocketsConsts.HANDSHAKE_EXISTING_FIELDS_FIELD_NAME,
                            String.join(",", handlerResponse.params.keySet()));
                    handlerResponse.params.forEach(builder::put);
                }

                // pass the host representation to the onOpen method
                conn.setAttachment(host);

                return builder;
            }
        };
    }

    @Override
    public void stop() {
        if (wsServer != null) {
            try {
                wsServer.stop(100);
                logger.info("WebsocketsServer stopped");
            } catch (InterruptedException e) {
                logger.warn("Exception occured while stopping WebSocket server: " + e);
            }
            wsServer = null;
        } else {
            logger.warn("Stopped before started. Nothing happens");
        }
    }

    @Override
    public void sendPacketAll(NetworkPacket packet) {
        sendPacket(packet, hostToSocket.keySet());
    }

    @Override
    public void sendPacket(NetworkPacket packet, Collection<Host> hosts) {
        if (wsServer != null) {
            String packetString = packetConverter.packetToString(packet);

            if (packetString != null) {
                Collection<WebSocket> toSockets = hosts.stream().map(hostToSocket::get).collect(Collectors.toList());
                try {
                    wsServer.broadcast(packetString, toSockets);
                    logger.debug("Packet broadcasted: " + packet);
                } catch (WebsocketNotConnectedException e) {
                    logger.warn("Websockets closed when sending packet");
                }
            } else {
                logger.error("Packet could not be broadcasted due to conversion failure");
            }
        } else {
            logger.warn("WebsocketsServer not instanciated when pushing packet: " + packet);
        }
    }

    // --- register handlers for events ---

    @Override
    public void onHandshake(HandshakeHandler handler) {
        handshakeHander = handler;
    }

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
