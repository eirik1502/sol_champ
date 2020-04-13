package sol_engine.network.network_sol_module;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sol_engine.module.Module;
import sol_engine.network.network_game.GameHost;
import sol_engine.network.network_game.PacketsQueue;
import sol_engine.network.network_game.game_server.NetworkGameServer;
import sol_engine.network.packet_handling.NetworkPacket;
import sol_engine.network.network_game.game_server.ServerConnectionData;

import java.util.*;

public class NetworkServerModule extends Module {
    private final Logger logger = LoggerFactory.getLogger(NetworkServerModule.class);

    private NetworkServerModuleConfig config;
    private ServerConnectionData connectionData = null;

    private NetworkGameServer server;

    private PacketsQueue currentPackets = new PacketsQueue();
    private Set<GameHost> currentConnections = new HashSet<>();
    private Set<GameHost> currentDisconnections = new HashSet<>();


    public NetworkServerModule(NetworkServerModuleConfig config) {
        this.config = config;
    }


    @SafeVarargs
    public final void usePacketTypes(Class<? extends NetworkPacket>... packetTypes) {
        usePacketTypes(Arrays.asList(packetTypes));
    }

    public final void usePacketTypes(List<Class<? extends NetworkPacket>> packetTypes) {
        if (server != null) {
            server.usePacketTypes(packetTypes);
        } else {
            logger.warn("calling usePacketTypes() before game server is setup");
        }
    }

    public NetworkGameServer getGameServer() {
        return server;
    }

    public Set<GameHost> getNewConnectedHosts() {
        return new HashSet<>(currentConnections);
    }

    public Set<GameHost> getNewDisconnectedHosts() {
        return new HashSet<>(currentDisconnections);
    }

    public Map<Class<? extends NetworkPacket>, Deque<NetworkPacket>> getCurrentPacketsForHost(GameHost host) {
        return currentPackets.peekForHost(host);
    }

    public <T extends NetworkPacket> Map<GameHost, Deque<T>> getCurrentPacketsOfType(Class<T> type) {
        return currentPackets.peekOfType(type);
    }

    public void sendPacketAll(NetworkPacket packet) {
        server.sendPacketAll(packet);
    }

    public void sendPacket(NetworkPacket packet, List<GameHost> hosts) {
        server.sendPacket(packet, hosts);
    }

    public void sendPacket(NetworkPacket packet, GameHost... hosts) {
        server.sendPacket(packet, hosts);
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
        connectionData = server.setup(config.gameServerConfig);
        server.usePacketTypes(config.packetTypes);
    }

    @Override
    public void onStart() {
        if (server != null) {
            server.start();
            if (config.waitForAllPlayerConnections) {
                logger.info("Server started, waiting for all connections");
                server.waitForAllPlayerConnections();
            }
        } else {
            logger.error("server not setup when onStart() is called");
        }
    }

    @Override
    public void onEnd() {
        server.stop();
    }

    @Override
    public void onUpdate() {
        currentPackets.clear();
        currentPackets.addAll(server.popInputPacketsQueue());

        currentConnections.clear();
        currentConnections.addAll(server.popNewConnections());

        currentDisconnections.clear();
        currentDisconnections.addAll(server.popNewDisconnections());
    }
}
