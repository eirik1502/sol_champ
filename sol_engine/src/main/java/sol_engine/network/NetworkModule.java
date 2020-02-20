package sol_engine.network;

import sol_engine.module.Module;
import sol_engine.network.client.NetworkClient;
import sol_engine.network.server.NetworkServer;
import sol_engine.network.server.NetworkWebsocketsServer;
import sol_engine.network_module.network_modules.NetworkServerModuleConfig;

import java.util.*;

public class NetworkModule extends Module {

    private NetworkModuleConfig config;

    private NetworkServer server = null;
    private NetworkClient client = null;
    private NetworkClassPacketLayer classPacketLayer;

    public NetworkModule(NetworkModuleConfig config) {
        this.config = config;
    }

    public void

    @Override

    public void onSetup() {

    }

    @Override
    public void onStart() {
        if (config.isServer) {
            server = new NetworkWebsocketsServer();
            classPacketLayer = new NetworkClassPacketLayer(server);

            server.start(config.port);
        } else {
            client = null;
            classPacketLayer = new NetworkClassPacketLayer(client);

            client.connect(config.address, config.port);
        }
    }

    @Override
    public void onEnd() {

    }

    @Override
    public void onUpdate() {

    }
}
