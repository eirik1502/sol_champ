package sol_engine.network_module.network_modules;

import sol_engine.modules.Module;
import sol_engine.network_module.NetPacket;
import sol_engine.network_module.connection_handler.PacketListener;
import sol_engine.network_module.connection_handler.ServerConnectionsHandler;
import sol_engine.network_module.connection_handler._ClientConnectionHandler;
import sol_engine.network_module.connection_handler._ConnectionsHandler;

public class NetworkModule extends Module {

    //TODO: dont store the config, only the data in the config
    private final NetworkModuleConfig config;
    private boolean isServer;

    _ConnectionsHandler connectionHandler;


    public NetworkModule(NetworkModuleConfig config) {
        this.config = config;
        this.isServer = config.isServer;
    }


    public void registerPacket(Class<? extends NetPacket> packetType) {
        connectionHandler.registerPacket(packetType);
    }

    public <T extends NetPacket> void registerPacketListener(Class<T> packetType, PacketListener<T> listener) {
        connectionHandler.registerPacketListener(packetType, listener);
    }

    public void sendToAll(NetPacket packet) {
        connectionHandler.sendToAll(packet);
    }

    public boolean isServer() {
        return isServer;
    }


    @Override
    public void onStart() {
        if (isServer) {
            connectionHandler = new ServerConnectionsHandler("server", config.port);
        } else {
            connectionHandler = new _ClientConnectionHandler("client", config.serverAddr, config.port);
        }


        // if the connection handler is a client, a connection should be established during the startListening process
        connectionHandler.start();
    }

    @Override
    public void onEnd() {

    }

    @Override
    public void onUpdate() {
        connectionHandler.pollNewConnections();
        connectionHandler.pollPackets();
    }

}
