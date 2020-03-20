package sol_engine.network.network_sol_module;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sol_engine.module.Module;
import sol_engine.network.network_game.GameHost;
import sol_engine.network.network_game.game_server.NetworkGameServer;
import sol_engine.network.packet_handling.NetworkPacket;
import sol_engine.network.network_game.game_server.ServerConnectionData;

import java.util.*;

public class NetworkServerModule extends Module {
    private final Logger logger = LoggerFactory.getLogger(NetworkServerModule.class);

    private NetworkServerModuleConfig config;
    private ServerConnectionData connectionData = null;

    private NetworkGameServer server;

    public NetworkServerModule(NetworkServerModuleConfig config) {
        this.config = config;
    }


    @SafeVarargs
    public final void usePacketTypes(Class<? extends NetworkPacket>... packetTypes) {
        usePacketTypes(Arrays.asList(packetTypes));
    }

    public final void usePacketTypes(List<Class<? extends NetworkPacket>> packetTypes) {
        server.usePacketTypes(packetTypes);
    }


    public <T extends NetworkPacket> Map<GameHost, Deque<T>> peekPacketsOfType(Class<T> packetType) {
        return server.peekPacketsOfType(packetType);
    }

    public void sendPacketAll(NetworkPacket packet) {
        server.sendPacketAll(packet);
    }

    public boolean isRunning() {
        return server.isRunning();
    }

    public ServerConnectionData getConnectionData() {
        if (connectionData == null) {
            logger.error("Cannot get connection data before internalSetup() is called");
        }
        return connectionData;
    }

    @Override
    public void onSetup() {
        server = new NetworkGameServer();
        server.usePacketTypes(config.packetTypes);
        this.connectionData = server.start(config.serverConfig);

        // wait for connections
        server.waitForAllPlayerConnections();
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onEnd() {
        server.stop();
    }

    @Override
    public void onUpdate() {
        server.clearAllPackets();
    }
}
