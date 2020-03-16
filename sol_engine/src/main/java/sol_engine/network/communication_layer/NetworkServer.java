package sol_engine.network.communication_layer;

import sol_engine.network.communication_layer.NetworkEndpoint;
import sol_engine.network.communication_layer.NetworkCommunicationLayer;
import sol_engine.network.server.ConnectingHost;
import sol_engine.network.server.Host;

import java.util.Set;

public interface NetworkServer extends NetworkCommunicationLayer, NetworkEndpoint {

    interface HandshakeHandler {
        Host handleHandshake(ConnectingHost connectingHost);
    }

    interface OpenHandler {
        boolean handleOpen(Host host);
    }

    interface CloseHandler {
        boolean handleClose(Host host);
    }

    void start(int port);

    void disconnectHost(Host host);

    Set<Host> getConnectedHosts();

    void onHandshake(HandshakeHandler handler);

    void onOpen(OpenHandler handler);

    void onClose(CloseHandler handler);
}