package sol_engine.network.network_game.game_server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sol_engine.network.communication_layer.Host;
import sol_engine.network.communication_layer.NetworkServer;
import sol_engine.network.network_game.GameHost;
import sol_engine.network.network_game.GameHostConnectionParams;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.stream.IntStream;

public class ServerConnectionManager implements NetworkServer.HandshakeHandler, NetworkServer.OpenHandler, NetworkServer.CloseHandler {
    public interface GameHostOpenedListener {
        void handleGameHostOpened(GameHost gameHost, Host host);
    }

    public interface HostClosedListener {
        void handleHostClosed(Host host);
    }

    public interface CheckTeamPlayerFree {
        boolean teamPlayerFree(int teamIndex, int playerIndex);
    }

    private final Logger logger = LoggerFactory.getLogger(ServerConnectionManager.class);

    private ServerConnectionData connectionData;
    private CheckTeamPlayerFree checkTeamPlayerFree;
    private GameHostOpenedListener gameHostOpenedListener = (a, b) -> {
    };
    private HostClosedListener hostClosedListener = (a) -> {
    };
    private Map<Host, GameHost> unopenedAcceptedHosts = Collections.synchronizedMap(new HashMap<>());  // hosts passed handshake but not yet opened


    public ServerConnectionManager(
            ServerConnectionData connectionData,
            CheckTeamPlayerFree checkTeamPlayerFree
    ) {
        this.connectionData = connectionData;
        this.checkTeamPlayerFree = checkTeamPlayerFree;
    }

    public void onGameHostOpen(GameHostOpenedListener listener) {
        gameHostOpenedListener = listener;
    }

    public void onHostClosed(HostClosedListener listener) {
        hostClosedListener = listener;
    }

    /**
     * To be called on handshake to validate the connecting host and create a Host representation
     *
     * @param host representing the connecting host
     * @return a Host representation of the connectingHost or null if the conneciton could not be established
     */
    @Override
    public synchronized NetworkServer.HandshakeResponse handleHandshake(Host host, Map<String, String> params) {
        GameHostConnectionParams connectionParams = GameHostConnectionParams.fromParmaMap(params);

        GameHost gameHost = verifyAndCreateGameHost(host, connectionParams);
        if (gameHost == null) {
            return new NetworkServer.HandshakeResponse(false, null);
        } else {
            Map<String, String> responseParams = Map.of(
                    "sessionId", Integer.toString(gameHost.sessionId),
                    "isObserver", Boolean.toString(gameHost.isObserver),
                    "teamIndex", Integer.toString(gameHost.teamIndex),
                    "playerIndex", Integer.toString(gameHost.playerIndex)
            );
            unopenedAcceptedHosts.put(host, gameHost);
            return new NetworkServer.HandshakeResponse(true, responseParams);
        }
    }

    /**
     * Call when connection is opened. Validates the host again
     * Synchronized to make sure the handling of the game host is performed before a new connection
     *
     * @param host that represents the connecting host
     * @return wether the connection is still valid
     */
    @Override
    public synchronized boolean handleOpen(Host host) {
        GameHost gameHost = unopenedAcceptedHosts.remove(host);

        // check if host was registered after handshake
        if (gameHost == null) {
            logger.info("Connection opened for host not registered in handshake, for host: " + host);
            return false;
        }

        if (!gameHost.isObserver) {
            if (!checkTeamPlayerFree.teamPlayerFree(gameHost.teamIndex, gameHost.playerIndex)) {
                logger.warn("Team player spot not free, after when connection is opened, after handshake. For connecting GameHost: " + gameHost);
                return false;
            }
        }

        logger.info("Connection opened for GameHost: " + gameHost);

        gameHostOpenedListener.handleGameHostOpened(gameHost, host);

        return true;
    }

    @Override
    public synchronized boolean handleClose(Host host) {
        GameHost gameHost = unopenedAcceptedHosts.remove(host);
        if (gameHost != null) {
            logger.warn("Connection closed before client was opened for GameHost: " + gameHost);
            return false;
        } else {
            hostClosedListener.handleHostClosed(host);
            logger.info("Connection closed for host: " + host);
            return true;
        }

    }


    private GameHost verifyAndCreateGameHost(Host connectingHost, GameHostConnectionParams params) {
        logger.info("Client initiating handshake. Host: " + connectingHost + ", with params: " + params);

        if (params.gameId.equals("") || params.connectionKey.equals("")) {
            logger.warn("gameId and/or connectionKey not present");
            return null;
        }

        if (!connectionData.gameId.equals(params.gameId)) {
            logger.warn("gameId invalid");
            return null;
        }

        int sessionId = createNewSessionId();

        // Observer host branch
        if (params.isObserver) {
            if (!connectionData.allowObservers) {
                logger.warn("observers are not allowed");
                return null;
            } else if (!params.connectionKey.equals(connectionData.observerKey)) {
                logger.warn("invalid observer connectionKey");
                return null;
            }

            return new GameHost(
                    connectingHost.address,
                    connectingHost.port,
                    sessionId,
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
                logger.warn("invalid player connectionKey");
                return null;
            }

            int playerIndex = connectionData.teamsPlayersKeys.get(teamIndex).indexOf(params.connectionKey);

            // check if the team player position is free
            if (!checkTeamPlayerFree.teamPlayerFree(teamIndex, playerIndex)) {
                logger.warn("the team player that is connected to is already present");
                return null;
            }

            return new GameHost(
                    connectingHost.address,
                    connectingHost.port,
                    sessionId,
                    params.name,
                    params.connectionKey,
                    false,
                    teamIndex,
                    playerIndex
            );
        }

    }

    private int nextSessionId = 0;

    private int createNewSessionId() {
        if (nextSessionId == Integer.MAX_VALUE) {
            logger.error("No more sessionIds, cannot handle more hosts");
            throw new IllegalStateException("No more sessionIds, cannot handle more hosts");
        }
        return nextSessionId++;
    }

}
