package sol_engine.network_module.network_modules;

import sol_engine.module.Module;
import sol_engine.network_module.connection_handler.ConnectionEstablisher;

public class NetworkClientModule extends Module {

    public static class Config {
        public int port;
        public String serverAddr;
    }


    private final Config config;


    public NetworkClientModule(Config config) {
        this.config = config;
    }

    @Override
    public void onStart() {
        ConnectionEstablisher connEst = new ConnectionEstablisher();
        connEst.connectToServer(config.serverAddr, config.port);
    }

    @Override
    public void onEnd() {

    }

    @Override
    public void onUpdate() {

    }
}
