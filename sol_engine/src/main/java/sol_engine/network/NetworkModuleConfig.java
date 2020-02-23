package sol_engine.network;

import sol_engine.network.server.ConnectionAcceptanceCriteria;

import java.util.ArrayList;
import java.util.List;

public abstract class NetworkModuleConfig {

    public boolean isServer;
    public int port;
    public String address;
    public List<Class<? extends ConnectionAcceptanceCriteria>> connectionAcceptanceCriteria = new ArrayList<>();


    public NetworkModuleConfig(boolean isServer, int port, String address, List<Class<? extends ConnectionAcceptanceCriteria>> connectionAcceptanceCriteria) {
        this.isServer = isServer;
        this.port = port;
        this.address = address;
        this.connectionAcceptanceCriteria = connectionAcceptanceCriteria;
    }
}

