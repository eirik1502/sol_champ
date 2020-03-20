package sol_engine.network.network_game.game_server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sol_engine.network.communication_layer.Host;
import sol_engine.network.communication_layer.NetworkServer;
import sol_engine.network.network_game.GameHost;
import sol_engine.network.network_game.PacketsQueueByHost;
import sol_engine.network.network_utils.NetworkUtils;
import sol_engine.network.packet_handling.NetworkPacket;
import sol_engine.network.communication_layer_impls.websockets.NetworkWebsocketsServer;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class NetworkGameServer {
    private final Logger logger = LoggerFactory.getLogger(NetworkGameServer.class);

    private ServerGameHostsManager hostsManager;
    private ServerConnectionData connectionData;


    private NetworkServer server;


    public ServerConnectionData setup(GameServerConfig config) {
        logger.info("setup with config: " + config);
        server = new NetworkWebsocketsServer();  // may use another server implementation

        connectionData = createConnectionData(config);
        hostsManager = new ServerGameHostsManager(connectionData);

        // assign handlers to the server
        server.onHandshake(hostsManager);
        server.onOpen(hostsManager);
        server.onClose(hostsManager);
        server.onPacket(hostsManager);

        logger.info("setup finished with connection data: " + connectionData);
        return connectionData;
    }

    public void usePacketTypes(List<Class<? extends NetworkPacket>> packetTypes) {
        if (server != null) {
            server.usePacketTypes(packetTypes);
        } else {
            logger.warn("calling usePacketTypes() before server is setup");
        }
    }

    public void start() {
        start(false);
    }

    public void start(boolean waitForAllPlayerConnections) {
        server.start(connectionData.port);
        if (waitForAllPlayerConnections) {
            waitForAllPlayerConnections();
        }
    }

    public boolean waitForAllPlayerConnections() {
        while (!hostsManager.getTeamPlayerHosts().allPlayersPresent()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                logger.warn("Thread interrupted while waiting for all players connections");
                return false;
            }
        }
        return true;
    }

    public TeamPlayerHosts getTeamPlayerHosts() {
        return new TeamPlayerHosts(hostsManager.getTeamPlayerHosts());
    }

    public Set<GameHost> getAllPlayerHosts() {
        return hostsManager.getAllPlayerHosts();
    }

    public Set<GameHost> getObserverHosts() {
        return hostsManager.getObserverHosts();
    }

    public Set<GameHost> getAllConnectedHosts() {
        return hostsManager.getAllConnectedHosts();
    }


    public PacketsQueueByHost peekPacketsForHost(GameHost host) {
        return hostsManager.peekPacketsForHost(host);
    }

    public <T extends NetworkPacket> Map<GameHost, Deque<T>> pollPacketsOfType(Class<T> type) {
        return new HashMap<>();
    }

    public void clearAllPackets() {

    }

    public void sendPacketAll(NetworkPacket packet) {
        server.sendPacketAll(packet);
    }

    public void sendPacket(NetworkPacket packet, List<Host> hosts) {
        server.sendPacket(packet, hosts);
    }

    public void sendPacket(NetworkPacket packet, Host... hosts) {
        server.sendPacket(packet, hosts);
    }


    public boolean isRunning() {
        return server.isRunning();
    }

    public void stop() {
        if (server != null) {
            server.stop();
        }
    }

    private ServerConnectionData createConnectionData(GameServerConfig config) {
        return new ServerConnectionData(
                NetworkUtils.uuid(),
                "localhost",
                config.port != -1 ? config.port : NetworkUtils.findFreeSocketPort(),
                config.teamSizes.stream()
                        .map(teamSize ->
                                IntStream.range(0, teamSize)
                                        .mapToObj(i -> NetworkUtils.uuid())
                                        .collect(Collectors.toList())
                        )
                        .collect(Collectors.toList()),
                config.allowObservers,
                config.allowObservers ? NetworkUtils.uuid() : ""
        );
    }
}
