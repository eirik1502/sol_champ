package sol_engine.network.network_game.game_server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sol_engine.network.communication_layer.Host;
import sol_engine.network.communication_layer.NetworkServer;
import sol_engine.network.network_game.GameHost;
import sol_engine.network.network_game.GameHostConnectionParams;
import sol_engine.network.network_utils.NetworkUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GameHostsManager implements NetworkServer.HandshakeHandler, NetworkServer.OpenHandler, NetworkServer.CloseHandler {
    private final Logger logger = LoggerFactory.getLogger(GameHostsManager.class);

    private ServerConnectionData connectionData;

    private Map<Host, GameHost> unopenedAcceptedHosts = new HashMap<>();
    private Map<Host, GameHost> openHosts = new HashMap<>();
    private TeamPlayerHosts teamPlayerHosts;


    public GameHostsManager(ServerConnectionData connectionData) {
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

    public Set<GameHost> getAllConnectedHosts() {
        return new HashSet<>(openHosts.values());
    }

    /**
     * To be called on handshake to validate the connecting host and create a Host representation
     *
     * @param host representing the connecting host
     * @return a Host representation of the connectingHost or null if the conneciton could not be established
     */
    @Override
    public boolean handleHandshake(Host host, Map<String, String> params) {
        GameHostConnectionParams connectionParams = GameHostConnectionParams.fromParmaMap(params);

        GameHost gameHost = verifyAndCreateGameHost(host, connectionParams);
        if (gameHost == null) {
            return false;
        } else {
            unopenedAcceptedHosts.put(host, gameHost);
            return true;
        }
    }

    /**
     * Call when connection is opened. Validates the host again
     *
     * @param host that represents the connecting host
     * @return wether the connection is still valid
     */
    @Override
    public boolean handleOpen(Host host) {
        // check if host was registered after handshake
        if (!unopenedAcceptedHosts.containsKey(host)) {
            logger.info("Connection opened for host not registered in handshake, for host: " + host);
            return false;
        }

        GameHost gameHost = unopenedAcceptedHosts.get(host);

        // check if the connection key was used between handshake and open
        if (teamPlayerHosts.checkConnectonKeyExists(gameHost.connectionKey)) {
            logger.info("ConnectionKey already used when connection is opened for host: " + host);
            return false;
        }

        unopenedAcceptedHosts.remove(host);
        openHosts.put(host, gameHost);
        teamPlayerHosts.setHost(gameHost.teamIndex, gameHost.teamPlayerIndex, gameHost);
        return true;
    }

    @Override
    public boolean handleClose(Host host) {
        GameHost gameHost = openHosts.get(host);
        if (!teamPlayerHosts.checkHostExists(gameHost)) {
            logger.warn("Disconnecting host was never connected");
        }
        teamPlayerHosts.replaceHost(gameHost, null);
        if (openHosts.containsKey(host)) {
            openHosts.remove(host);
        } else {
            unopenedAcceptedHosts.remove(host);
        }
        return true;
    }


    private GameHost verifyAndCreateGameHost(Host connectingHost, GameHostConnectionParams params) {
        if (params.gameId.equals("") || params.connectionKey.equals("")) {
            logger.info("gameId and/or connectionKey not present");
            return null;
        }

        if (!connectionData.gameId.equals(params.gameId)) {
            logger.info("gameId invalid");
            return null;
        }

        String connectionId = NetworkUtils.uuid();

        // Observer host branch
        if (params.isObserver) {
            if (!connectionData.allowObservers) {
                logger.info("observers are not allowed");
                return null;
            } else if (!params.connectionKey.equals(connectionData.observerKey)) {
                logger.info("invalid observer connectionKey");
                return null;
            }

            return new GameHost(
                    connectingHost.address,
                    connectingHost.port,
                    connectionId,
                    params.name,
                    params.connectionKey,
                    true,
                    -1,
                    -1
            );
        }

        // Player host branch
        else {
            int teamIndex = IntStream.range(0, connectionData.teamsPlayersKeys.size())
                    .filter(ti -> connectionData.teamsPlayersKeys.get(ti).contains(params.connectionKey))
                    .findAny()
                    .orElse(-1);
            if (teamIndex == -1) {
                logger.info("invalid player connectionKey");
                return null;
            }

            int teamPlayerIndex = connectionData.teamsPlayersKeys.get(teamIndex).indexOf(params.connectionKey);

            // check if the key is already used
            if (teamPlayerHosts.checkConnectonKeyExists(params.connectionKey)) {
                logger.info("player connectionKey already used");
                return null;
            }

            return new GameHost(
                    connectingHost.address,
                    connectingHost.port,
                    connectionId,
                    params.name,
                    params.connectionKey,
                    false,
                    teamIndex,
                    teamPlayerIndex
            );
        }

    }

}
