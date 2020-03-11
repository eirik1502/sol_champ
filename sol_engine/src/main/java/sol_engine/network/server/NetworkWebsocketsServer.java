package sol_engine.network.server;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import sol_engine.network.packet_handling.NetworkPacketRaw;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;

public class NetworkWebsocketsServer implements NetworkServer {
    private final Logger logger = LoggerFactory.getLogger(NetworkWebsocketsServer.class);

    private ConnectionData connectionData;

    private List<ConnectionAcceptanceCriteria> connectionAcceptanceCriteria = new ArrayList<>();
    private ObjectMapper jsonMapper = new ObjectMapper();
    private HashMap<WebSocket, Host> connectedHosts = new HashMap<>();
    private Deque<NetworkPacketRaw> inputPacketQueue = new ArrayDeque<>();
    private boolean terminated = false;

    private WebSocketServer wsServer;


    @Override
    public void addConnectionAcceptanceCriteria(ConnectionAcceptanceCriteria criteria) {
        connectionAcceptanceCriteria.add(criteria);
    }

    @Override
    public ConnectionData start() {
        int port = NetworkUtils.findFreeSocketPort();
        ConnectionData connectionData = new ConnectionData(
                NetworkUtils.uuid(),
                "localhost",
                port,
                List.of(
                        List.of(NetworkUtils.uuid()),
                        List.of(NetworkUtils.uuid())
                ),
                true,
                NetworkUtils.uuid()
        );
        addConnectionAcceptanceCriteria(new PlayersConnectionCriteria(connectionData));
        wsServer = createWsServer(port);
        wsServer.start();
        return connectionData;
    }

    @Override
    public boolean isConnected() {
        return wsServer != null;
    }

    private WebSocketServer createWsServer(int port) {

        NetworkServer thisServer = this;
        return new WebSocketServer(new InetSocketAddress(port)) {
            @Override
            public void onOpen(WebSocket conn, ClientHandshake handshake) {

                if (!connectedHosts.containsKey(conn)) {
                    Host host = conn.getAttachment();
                    connectedHosts.put(conn, host);
                    logger.info("Host connected: " + host);
                } else {
                    logger.warn("connecting host is already connected, after handshake");
                }

            }

            @Override
            public void onClose(WebSocket conn, int code, String reason, boolean remote) {
                if (connectedHosts.containsKey(conn)) {
                    connectedHosts.remove(conn);
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

                // create a host representation of the connecting host
                Host host = new Host(
                        "Fred",
                        UUID.randomUUID().toString(), // host id
                        conn.getRemoteSocketAddress().getAddress().toString(),
                        conn.getRemoteSocketAddress().getPort()
                );

                // pass the host representation to the onOpen method
                conn.setAttachment(host);

                boolean accepted = connectionAcceptanceCriteria.stream()
                        .allMatch(c -> c.accepted(thisServer, host, request.getResourceDescriptor()));
                if (!accepted) {
                    throw new InvalidDataException(CloseFrame.POLICY_VALIDATION, "host not accepted");
                }
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
        }
    }

    @Override
    public Deque<NetworkPacketRaw> pollPackets() {
        Deque<NetworkPacketRaw> packets = new ArrayDeque<>(inputPacketQueue);
        inputPacketQueue.clear();
        return packets;
    }

    @Override
    public void pushPacket(String packet) {
        if (wsServer != null) {
            wsServer.broadcast(packet);
            logger.info("Packet pushed: " + packet);
        } else {
            logger.warn("WebsocketsServer not instanciated when pushing packet: " + packet);
        }
    }
}
