package sol_engine.network.client;

import sol_engine.network.communication_layer.NetworkClient;
import sol_engine.network.communication_layer_impls.websockets.NetworkWebsocketsClient;
import sol_engine.network.packet_handling.NetworkPacket;
import sol_engine.network.server.Host;

import java.util.Map;

public class NetworkGameClient implements NetworkClient.OpenHandler, NetworkClient.CloseHandler {

    private NetworkClient client;

    public boolean connect(ClientConfig config) {
        Map<String, String> connectParams = Map.of(
                "gameId", config.gameId,
                "connectionKey", config.connectionKey,
                "isObserver", Boolean.toString(config.isObserver)
        );

        client = new NetworkWebsocketsClient();

        client.onOpen(this);
        client.onClose(this);

        boolean connected = client.connect(config.address, config.port, connectParams);
        return connected;
    }

    public void terminate() {
        if (client != null) {
            client.terminate();  // assumed to call handleClose
        }
    }

    public void sendPacket(NetworkPacket packet) {
        client.sendPacketAll(packet);
    }

    public boolean isConnected() {
        if (client != null) {
            return client.isConnected();
        } else {
            return false;
        }
    }


    @Override
    public void handleOpen() {
    }

    @Override
    public void handleClose() {
        client = null;
    }
}
