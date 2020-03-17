package sol_engine.network.communication_layer;

import java.util.Map;
import java.util.Set;

public interface NetworkServer extends NetworkCommunicationServer, NetworkEndpoint {

    interface HandshakeHandler {
        boolean handleHandshake(Host host, Map<String, String> params);
    }

    interface OpenHandler {
        boolean handleOpen(Host host);
    }

    interface CloseHandler {
        boolean handleClose(Host host);
    }

    void start(int port);

    void stop();

    void disconnectHost(Host host);

    Set<Host> getConnectedHosts();

    void onHandshake(HandshakeHandler handler);

    void onOpen(OpenHandler handler);

    void onClose(CloseHandler handler);
}