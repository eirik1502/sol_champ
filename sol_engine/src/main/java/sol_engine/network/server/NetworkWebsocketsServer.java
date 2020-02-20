package sol_engine.network.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.exceptions.InvalidDataException;
import org.java_websocket.framing.CloseFrame;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.ServerHandshakeBuilder;
import org.java_websocket.server.WebSocketServer;
import sol_engine.network.NetworkPacketRaw;

import java.io.IOException;
import java.util.*;

public class NetworkWebsocketsServer implements NetworkServer {

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
    public void start(int port) {
        wsServer = createWsServer();
        wsServer.start();
    }

    private WebSocketServer createWsServer() {
        NetworkServer thisServer = this;
        return new WebSocketServer() {
            @Override
            public void onOpen(WebSocket conn, ClientHandshake handshake) {
                if (!connectedHosts.containsKey(conn)) {
                    Host host = conn.getAttachment();
                    connectedHosts.put(conn, host);
                } else {
                    // log host already connected
                }

            }

            @Override
            public void onClose(WebSocket conn, int code, String reason, boolean remote) {
                if (connectedHosts.containsKey(conn)) {
                    connectedHosts.remove(conn);
                } else {
                    // log disconnecting host was not connected
                }
            }

            @Override
            public void onMessage(WebSocket conn, String message) {
                if (connectedHosts.containsKey(conn)) {
                    inputPacketQueue.add(new NetworkPacketRaw(connectedHosts.get(conn), message));
                } else {
                    // log
                }
            }

            @Override
            public void onError(WebSocket conn, Exception ex) {
                // log
            }

            @Override
            public void onStart() {

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
    public void waitForConnections(int count) {

    }

    @Override
    public void terminate() {
        if (wsServer != null) {
            try {
                wsServer.stop();
            } catch (IOException | InterruptedException e) {
                // log
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
        }
    }
}
