package sol_engine.network_module.network_modules;

import sol_engine.module.Module;
import sol_engine.network_module.NetPacket;
import sol_engine.network_module.connection_handler.ConnectedHost;
import sol_engine.network_module.connection_handler.ConnectionListener;

import java.util.ArrayList;
import java.util.List;

public class NetworkServerModule extends Module {

    public static class Config {
        public int port;
    }

    private final Config config;
    private final ConnectionListener connectionListener;

    private final List<ConnectedHost> connectedHosts;


    public NetworkServerModule(Config config) {
        this.config = config;
        connectionListener = new ConnectionListener();
        connectedHosts = new ArrayList<>();
    }

    @Override
    public void onStart() {
        connectionListener.startListening(config.port);
    }

    @Override
    public void onEnd() {
    }

    @Override
    public void onUpdate() {
        List<ConnectedHost> newConnections = connectionListener.pollNewConnections();
        connectedHosts.addAll(newConnections);
    }

    public void sendToAll(NetPacket packet) {
        connectedHosts.forEach(host -> host.send(packet));
    }

    public List<NetPacket> pollAllPackets() {
        return new ArrayList<>();
    }
}
