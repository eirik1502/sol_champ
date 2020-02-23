package sol_engine.network.client;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sol_engine.network.packet_handling.NetworkPacketRaw;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Deque;

public class NetworkWebsocketsClient implements NetworkClient {
    private final Logger logger = LoggerFactory.getLogger(NetworkWebsocketsClient.class);

    private WebSocketClient wsClient;

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

            }

            @Override
            public void onMessage(String message) {

            }

            @Override
            public void onClose(int code, String reason, boolean remote) {

            }

            @Override
            public void onError(Exception ex) {

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
        return null;
    }

    @Override
    public void pushPacket(String packet) {

    }
}
