package sol_engine.network.client;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sol_engine.network.packet_handling.NetworkPacketRaw;
import sol_engine.network.server.Host;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayDeque;
import java.util.Deque;


//TODO: should handle host corresponding to hosts connected to the server
public class NetworkWebsocketsClient implements NetworkClient {
    private final Logger logger = LoggerFactory.getLogger(NetworkWebsocketsClient.class);

    private WebSocketClient wsClient;
    private Host serverHost;
    private Deque<NetworkPacketRaw> pendingPackets = new ArrayDeque<>();

    @Override
    public boolean connect(String address, int port) {
        URI uri;
        try {
            uri = new URI(String.format("ws://%s:%d", address, port));
        } catch (URISyntaxException e) {
            logger.error("connection uri invalid: " + e);
            return false;
        }

        wsClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                serverHost = new Host("server", "server", address, port);
                logger.info("Client connected to server at address: " + wsClient.getRemoteSocketAddress());
            }

            @Override
            public void onMessage(String message) {
                pendingPackets.add(new NetworkPacketRaw(serverHost, message));
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                logger.info("Connection closed, status: " + code + " because: " + reason);
            }

            @Override
            public void onError(Exception ex) {
                logger.info("An error occured in WebsocketsClient: " + ex);
            }
        };

        wsClient.connect();
        return true;
    }

    @Override
    public void terminate() {
        if (wsClient != null) {
            wsClient.close();
        }
    }

    @Override
    public Deque<NetworkPacketRaw> pollPackets() {
        Deque<NetworkPacketRaw> packets = new ArrayDeque<>(pendingPackets);
        pendingPackets.clear();
        return packets;
    }

    @Override
    public void pushPacket(String packet) {
        if (wsClient != null) {
            wsClient.send(packet);
            logger.info("packet pushed: " + packet);
        } else {
            logger.warn("WebsocketsClient not instanciated when pushing packet: " + packet);
        }
    }

    @Override
    public boolean isConnected() {
        return wsClient != null && wsClient.isOpen();
    }
}
