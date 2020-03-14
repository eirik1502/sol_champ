package sol_engine.network.server;

import sol_engine.network.packet_handling.NetworkEndpoint;
import sol_engine.network.packet_handling.NetworkPacketLayer;

import java.util.Set;

public interface NetworkServer extends NetworkPacketLayer, NetworkEndpoint {

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

    void waitForConnections();

    Set<Host> getConnectedHosts();

    void onHandshake(HandshakeHandler handler);

    void onOpen(OpenHandler handler);

    void onClose(CloseHandler handler);
}