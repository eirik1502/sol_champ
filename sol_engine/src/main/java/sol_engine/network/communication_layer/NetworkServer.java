package sol_engine.network.communication_layer;

import java.util.Map;
import java.util.Set;

public interface NetworkServer extends NetworkCommunicationServer, NetworkEndpoint {

    class HandshakeResponse {
        public boolean accepted;
        public Map<String, String> params;

        public HandshakeResponse(boolean accepted, Map<String, String> params) {
            this.accepted = accepted;
            this.params = params;
        }
    }

    interface HandshakeHandler {
        HandshakeResponse handleHandshake(Host host, Map<String, String> params);
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