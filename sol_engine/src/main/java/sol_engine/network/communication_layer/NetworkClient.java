package sol_engine.network.communication_layer;

import java.util.Map;

public interface NetworkClient extends NetworkCommunicationClient, NetworkEndpoint {

    interface OpenHandler {
        void handleOpen();
    }

    interface CloseHandler {
        void handleClose();
    }

    boolean connect(String address, int port);

    boolean connect(String address, int port, Map<String, String> params);

    void disconnect();

    int getLocalPort();

    void onOpen(OpenHandler handler);

    void onClose(CloseHandler handler);
}
