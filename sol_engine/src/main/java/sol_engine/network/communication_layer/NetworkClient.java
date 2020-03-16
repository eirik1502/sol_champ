package sol_engine.network.communication_layer;

import sol_engine.network.communication_layer.NetworkEndpoint;
import sol_engine.network.communication_layer.NetworkCommunicationLayer;
import sol_engine.network.server.Host;

import java.util.Map;

public interface NetworkClient extends NetworkCommunicationLayer, NetworkEndpoint {

    interface OpenHandler {
        void handleOpen();
    }

    interface CloseHandler {
        void handleClose();
    }

    boolean connect(String address, int port, Map<String, String> params);

    void disconnect();

    void onOpen(OpenHandler handler);

    void onClose(CloseHandler handler);
}
