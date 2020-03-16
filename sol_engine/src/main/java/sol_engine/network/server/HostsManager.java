package sol_engine.network.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sol_engine.network.communication_layer.NetworkServer;
import sol_engine.network.network_utils.NetworkUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HostsManager implements NetworkServer.HandshakeHandler, NetworkServer.OpenHandler, NetworkServer.CloseHandler {
    private final Logger logger = LoggerFactory.getLogger(HostsManager.class);

    private ServerConnectionData connectionData;

    private Set<Host> handshakeAcceptedHosts = new HashSet<>();
    private TeamPlayerHosts teamPlayerHosts;


    public HostsManager(ServerConnectionData connectionData) {
        this.connectionData = connectionData;
        teamPlayerHosts = new TeamPlayerHosts(
                connectionData.teamsPlayersKeys.stream()
                        .map(List::size)
                        .collect(Collectors.toList())
        );
    }

    public TeamPlayerHosts getTeamPlayerHosts() {
        return teamPlayerHosts;
    }

    @Override
    public boolean handleClose(Host host) {
        if (!teamPlayerHosts.checkHostExists(host)) {
            logger.warn("Disconnecting host was never connected");
        }
        teamPlayerHosts.replaceHost(host, null);
        return true;
    }

    /**
     * Call when connection is opened. Validates the host again
     *
     * @param host that represents the connecting host
     * @return wether the connection is still valid
     */
    @Override
    public boolean handleOpen(Host host) {
        if (teamPlayerHosts.checkConnectonKeyExists(host.connectionKey)) {
            logger.info("ConnectionKey already used when connection is opened for host: " + host);
            return false;
        }

        if (!handshakeAcceptedHosts.contains(host)) {
            logger.info("Connection opened for host not registered in handshake, for host: " + host);
            return false;
        }

        handshakeAcceptedHosts.remove(host);
        teamPlayerHosts.setHost(host.teamIndex, host.teamPlayerIndex, host);
        return true;
    }

    /**
     * To be called on handshake to validate the connecting host and create a Host representation
     *
     * @param connectingHost representing the connecting host
     * @return a Host representation of the connectingHost or null if the conneciton could not be established
     */
    @Override
    public Host handleHandshake(ConnectingHost connectingHost) {
        if (connectingHost.gameId.equals("") || connectingHost.connectionKey.equals("")) {
            logger.info("gameId and/or connectionKey not present");
            return null;
        }

        if (!connectionData.gameId.equals(connectingHost.gameId)) {
            logger.info("gameId invalid");
            return null;
        }

        if (connectingHost.isObserver) {
            if (!connectionData.allowObservers) {
                logger.info("observers are not allowed");
                return null;
            } else if (!connectingHost.connectionKey.equals(connectionData.observerKey)) {
                logger.info("invalid observer connectionKey");
                return null;
            }

            String connectionId = NetworkUtils.uuid();
            Host host = new Host(
                    "_name_",
                    connectingHost.connectionKey,
                    connectionId,
                    connectingHost.address,
                    connectingHost.port,
                    true,
                    -1,
                    -1
            );

            handshakeAcceptedHosts.add(host);
            return host;

        } else {
            int teamIndex = IntStream.range(0, connectionData.teamsPlayersKeys.size())
                    .filter(ti -> connectionData.teamsPlayersKeys.get(ti).contains(connectingHost.connectionKey))
                    .findAny()
                    .orElse(-1);
            if (teamIndex == -1) {
                logger.info("invalid player connectionKey");
                return null;
            }

            int teamPlayerIndex = connectionData.teamsPlayersKeys.get(teamIndex).indexOf(connectingHost.connectionKey);

            // check if the key is already used
            if (teamPlayerHosts.checkConnectonKeyExists(connectingHost.connectionKey)) {
                logger.info("player connectionKey already used");
                return null;
            }

            String playerId = NetworkUtils.uuid();

            Host host = new Host(
                    "_name_",
                    connectingHost.connectionKey,
                    playerId,
                    connectingHost.address,
                    connectingHost.port,
                    false,
                    teamIndex,
                    teamPlayerIndex
            );

            handshakeAcceptedHosts.add(host);
            return host;
        }
    }

}
