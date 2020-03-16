package sol_engine.network.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sol_engine.network.communication_layer.NetworkCommunicationLayer;
import sol_engine.network.communication_layer.NetworkServer;
import sol_engine.network.network_utils.NetworkUtils;
import sol_engine.network.packet_handling.NetworkPacket;
import sol_engine.network.communication_layer_impls.websockets.NetworkWebsocketsServer;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class NetworkGameServer implements NetworkCommunicationLayer.PacketHandler {
    private final Logger logger = LoggerFactory.getLogger(NetworkGameServer.class);

    private HostsManager hostsManager;
    private Deque<NetworkPacket> inputPacketQueue = new ArrayDeque<>();

    private NetworkServer server;


    public ServerConnectionData start(ServerConfig config) {
        server = new NetworkWebsocketsServer();  // may use another server implementation

        ServerConnectionData connectionData = createConnectionData(config);
        hostsManager = new HostsManager(connectionData);

        // assign handlers to the server
        server.onHandshake(hostsManager);
        server.onOpen(hostsManager);
        server.onClose(hostsManager);
        server.onPacket(this);

        server.start(connectionData.port);
        return connectionData;
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

    @Override
    public void handlePacket(NetworkPacket packet, Host host) {

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


    public boolean isConnected() {
        return server.isConnected();
    }

    public void terminate() {
        if (server != null) {
            server.terminate();
        }
    }

    private ServerConnectionData createConnectionData(ServerConfig config) {
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
