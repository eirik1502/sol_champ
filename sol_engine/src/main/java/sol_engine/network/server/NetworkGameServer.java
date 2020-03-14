package sol_engine.network.server;

import sol_engine.network.network_utils.NetworkUtils;
import sol_engine.network.packet_handling.NetworkEndpoint;
import sol_engine.network.packet_handling.NetworkPacket;
import sol_engine.network.packet_handling.NetworkPacketRaw;
import sol_engine.network.websockets.NetworkWebsocketsServer;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class NetworkGameServer implements NetworkEndpoint {

    private HostsManager hostsManager;
    private Deque<NetworkPacketRaw> inputPacketQueue = new ArrayDeque<>();


    private NetworkServer server;


    public ServerConnectionData start(ServerConfig config) {
        server = new NetworkWebsocketsServer();  // may use another server implementation

        ServerConnectionData connectionData = createConnectionData(config);
        hostsManager = new HostsManager(connectionData);
        server.onHandshake(hostsManager);
        server.onOpen(hostsManager);
//        server.start();
        return connectionData;
    }

    public TeamPlayerHosts getTeamPlayerHosts() {
        return new TeamPlayerHosts(hostsManager.getTeamPlayerHosts());
    }

    public void sendPacketAll(NetworkPacket packet) {
        server.sendPacketAll(packet);
    }

    public void sendPacket(NetworkPacket packet, List<Host> hosts) {
        server.sendPacket(packet, hosts);
    }

    public void sendPacket(NetworkPacket packet, Host host) {
        server.sendPacket(packet, host);
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

    @Override
    public boolean isConnected() {
        return server.isConnected();
    }

    @Override
    public void terminate() {

    }
}
