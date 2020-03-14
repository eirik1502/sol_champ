package sol_engine.network.websockets;

import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.exceptions.InvalidDataException;
import org.java_websocket.framing.CloseFrame;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.ServerHandshakeBuilder;
import org.java_websocket.server.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sol_engine.network.network_utils.NetworkUtils;
import sol_engine.network.packet_handling.NetworkPacket;
import sol_engine.network.packet_handling.NetworkPacketLayer;
import sol_engine.network.packet_handling.NetworkPacketRaw;
import sol_engine.network.server.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class NetworkWebsocketsServer implements NetworkServer, NetworkPacketLayer {
    private final Logger logger = LoggerFactory.getLogger(NetworkWebsocketsServer.class);

    private HandshakeHandler handshakeHander;
    private OpenHandler openHandler;
    private CloseHandler closeHandler;
    private PacketHandler packetHandler;

    private HashMap<WebSocket, Host> socketToHost = new HashMap<>();
    private HashMap<Host, WebSocket> hostToSocket = new HashMap<>();

    private WebSocketServer wsServer;


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
    public boolean isConnected() {
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
                }
            }

            @Override
            public void onClose(WebSocket conn, int code, String reason, boolean remote) {
                if (socketToHost.containsKey(conn)) {
                    Host host = socketToHost.get(conn);

                    closeHandler.handleClose(host);

                    socketToHost.remove(conn);
                    hostToSocket.remove(host);
                } else {
                    logger.warn("Disconnecting host was never connected");
                }
            }

            @Override
            public void onMessage(WebSocket conn, String message) {
                if (connectedHosts.containsKey(conn)) {
                    inputPacketQueue.add(new NetworkPacketRaw(connectedHosts.get(conn), message));
                } else {
                    logger.warn("Got message from a host that is not connected");
                }
            }

            @Override
            public void onError(WebSocket conn, Exception ex) {
                logger.warn("An error occured: " + ex);
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

                Map<String, String> query = NetworkUtils.parseQueryParams(conn.getResourceDescriptor());

                ConnectingHost connectingHost = new ConnectingHost(
                        conn.getRemoteSocketAddress().getAddress().toString(),
                        conn.getRemoteSocketAddress().getPort(),
                        query.getOrDefault("gameId", ""),
                        query.getOrDefault("connectionKey", ""),
                        Boolean.parseBoolean(query.getOrDefault("isObserver", "false")),
                        "__NAME__"
                );

                Host host = handshakeHander.handleHandshake(connectingHost);

                if (host == null) {
                    throw new InvalidDataException(CloseFrame.POLICY_VALIDATION, "host not accepted");
                }

                // pass the host representation to the onOpen method
                conn.setAttachment(host);

                return builder;
            }
        };
    }

    @Override
    public void waitForConnections() {

    }

    @Override
    public void terminate() {
        if (wsServer != null) {
            try {
                wsServer.stop();
                logger.info("WebsocketsServer stopped");
            } catch (IOException | InterruptedException e) {
                logger.warn("Exception occured while stopping WebSocket server: " + e);
            }
            wsServer = null;
        }
    }

    @Override
    public Deque<NetworkPacketRaw> pollPackets() {
        Deque<NetworkPacketRaw> packets = new ArrayDeque<>(inputPacketQueue);
        inputPacketQueue.clear();
        return packets;
    }


    @Override
    public void sendPacketAll(NetworkPacket packet) {
        if (wsServer != null) {
            wsServer.broadcast(packet);
            logger.info("Packet pushed: " + packet);
        } else {
            logger.warn("WebsocketsServer not instanciated when pushing packet: " + packet);
        }
    }

    @Override
    public void sendPacket(NetworkPacket packet, List<Host> hosts) {

    }

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
