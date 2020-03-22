package sol_engine.network.network_game.game_server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sol_engine.network.communication_layer.Host;
import sol_engine.network.communication_layer.NetworkServer;
import sol_engine.network.network_game.GameHost;
import sol_engine.network.network_game.PacketsQueue;
import sol_engine.network.network_game.PacketsQueueByHost;
import sol_engine.network.network_game.PacketsQueueByType;
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
            if (server == null) {
                logger.info("Server stopped while waiting for all connections");
                return false;
            }
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

    public Deque<GameHost> peekNewConnections() {
        return hostsManager.peekNewConnectedHosts();
    }

    public Deque<GameHost> popNewConnections() {
        return hostsManager.popNewConnectedHosts();
    }

    public PacketsQueue peekInputPacketsQueue() {
        return hostsManager.peekInputPacketQueue();
    }

    public PacketsQueue popInputPacketsQueue() {
        return hostsManager.popInputPacketQueue();
    }

//    public PacketsQueueByType peekPacketsForHost(GameHost host) {
//        return hostsManager.peekPacketsForHost(host);
//    }
//
//    public <T extends NetworkPacket> PacketsQueueByHost<T> peekPacketsOfType(Class<T> type) {
//        return hostsManager.peekPacketsOfType(type);
//    }
//
//    public PacketsQueueByType pollPacketsForHost(GameHost host) {
//        return hostsManager.pollPacketsForHost(host);
//    }
//
//    public <T extends NetworkPacket> PacketsQueueByHost<T> pollPacketsOfType(Class<T> type) {
//        return hostsManager.pollPacketsOfType(type);
//    }

    public void sendPacketAll(NetworkPacket packet) {
        server.sendPacketAll(packet);
    }

    public void sendPacket(NetworkPacket packet, List<GameHost> hosts) {
        hosts.stream()
                .map(ghost -> hostsManager.getOpenHost(ghost))
                .filter(Objects::nonNull)
                .forEach(h -> server.sendPacket(packet, h));
    }

    public void sendPacket(NetworkPacket packet, GameHost... hosts) {
        sendPacket(packet, List.of(hosts));
    }


    public boolean isRunning() {
        return server.isRunning();
    }

    public void stop() {
        if (server != null) {
            server.stop();
            server = null;
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
