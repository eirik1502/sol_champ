package sol_engine.network.network_game.game_server;

import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sol_engine.network.communication_layer.Host;
import sol_engine.network.communication_layer.NetworkCommunicationServer;
import sol_engine.network.communication_layer.NetworkServer;
import sol_engine.network.network_game.GameHost;
import sol_engine.network.network_game.GameHostConnectionParams;
import sol_engine.network.network_game.PacketsQueueByHost;
import sol_engine.network.network_game.PacketsQueueByType;
import sol_engine.network.packet_handling.NetworkPacket;
import sol_engine.utils.collections.Pair;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ServerGameHostsManager implements NetworkServer.HandshakeHandler, NetworkServer.OpenHandler,
        NetworkServer.CloseHandler, NetworkCommunicationServer.PacketHandler {
    private final Logger logger = LoggerFactory.getLogger(ServerGameHostsManager.class);
    private static int nextSessionId = 0;

    private ServerConnectionData connectionData;

    private Map<Host, GameHost> unopenedAcceptedHosts = new HashMap<>();  // hosts passed handshake but not yet opened
    private Map<Host, GameHost> openHosts = new HashMap<>();  // all open hosts
    private TeamPlayerHosts teamPlayerHosts;  // open player hosts
    private Set<GameHost> observerHosts = new HashSet<>();  // open observer hosts

    private Map<GameHost, PacketsQueueByType> inputPacketQueue = new HashMap<>();


    public ServerGameHostsManager(ServerConnectionData connectionData) {
        this.connectionData = connectionData;
        teamPlayerHosts = new TeamPlayerHosts(
                connectionData.teamsPlayersKeys.stream()
                        .map(List::size)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public void handlePacket(NetworkPacket packet, Host host) {
        if (openHosts.containsKey(host)) {
            GameHost gameHost = openHosts.get(host);
            if (teamPlayerHosts.hasHost(gameHost)) {
                inputPacketQueue
                        .get(gameHost)
                        .add(packet);
            } else {
                logger.warn("Got a packet from an observer client, will be discarded. Game host: " + gameHost + ", packet: " + packet);
            }
        } else {
            logger.warn("Got a packet from a non-open host: " + host);
        }
    }

    public PacketsQueueByType peekPacketsForHost(GameHost host) {
        return new PacketsQueueByType(inputPacketQueue.get(host));
    }

    public <T extends NetworkPacket> PacketsQueueByHost<T> peekPacketsOfType(Class<T> type) {
        return inputPacketQueue.entrySet().stream()
                .flatMap(entry -> entry.getValue()
                        .peekAll(type).stream()
                        .map(packetOfType -> new Pair<>(entry.getKey(), packetOfType))
                )
                .collect(PacketsQueueByHost.pairCollector());
    }

    public TeamPlayerHosts getTeamPlayerHosts() {
        return teamPlayerHosts;
    }

    public Set<GameHost> getAllPlayerHosts() {
        return teamPlayerHosts.getAllPlayerHosts();
    }

    public Set<GameHost> getObserverHosts() {
        return new HashSet<>(observerHosts);
    }

    public Set<GameHost> getAllConnectedHosts() {
        return new HashSet<>(openHosts.values());
    }

    private Set<GameHost> getAllPendingHosts() {
        return new HashSet<>(unopenedAcceptedHosts.values());
    }

    private Set<GameHost> getAllConnectedAndPendingHosts() {
        return Sets.union(getAllConnectedHosts(), getAllPendingHosts()).immutableCopy();
    }

    /**
     * To be called on handshake to validate the connecting host and create a Host representation
     *
     * @param host representing the connecting host
     * @return a Host representation of the connectingHost or null if the conneciton could not be established
     */
    @Override
    public NetworkServer.HandshakeResponse handleHandshake(Host host, Map<String, String> params) {
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

        GameHost gameHost = unopenedAcceptedHosts.remove(host);

        if (gameHost.isObserver) {
            observerHosts.add(gameHost);
        } else {
            // check if the connection key was used between handshake and open
            if (teamPlayerHosts.checkConnectonKeyExists(gameHost.connectionKey)) {
                logger.info("ConnectionKey already used when connection is opened for host: " + host);
                return false;
            } else {
                teamPlayerHosts.setHost(gameHost.teamIndex, gameHost.playerIndex, gameHost);
                inputPacketQueue.putIfAbsent(gameHost, new PacketsQueueByType());  // create an input packet entry for the host
            }
        }
        openHosts.put(host, gameHost);
        return true;
    }

    @Override
    public boolean handleClose(Host host) {
        if (unopenedAcceptedHosts.containsKey(host)) {
            GameHost gameHost = unopenedAcceptedHosts.get(host);
            unopenedAcceptedHosts.remove(host);
            logger.info("Connection closed before client was opened for GameHost: " + gameHost);
        } else if (openHosts.containsKey(host)) {
            GameHost gameHost = openHosts.get(host);

            if (gameHost.isObserver) {
                observerHosts.remove(gameHost);
            } else {
                teamPlayerHosts.replaceHost(gameHost, null);
                inputPacketQueue.remove(gameHost);  // remove packet queue entry
            }
            openHosts.remove(host);
            logger.info("Connection closed for GameHost: " + gameHost);
        } else {
            logger.warn("Disconnecting host was never connected");
        }
        return true;
    }

    private int createNewSessionId() {
        if (nextSessionId == Integer.MAX_VALUE) {
            logger.error("No more sessionIds, cannot handle more hosts");
            throw new IllegalStateException("No more sessionIds, cannot handle more hosts");
        }
        return nextSessionId++;
    }

    private GameHost verifyAndCreateGameHost(Host connectingHost, GameHostConnectionParams params) {
        logger.info("Client initiating handshake. Host: " + connectingHost + ", with params: " + params);

        if (params.gameId.equals("") || params.connectionKey.equals("")) {
            logger.info("gameId and/or connectionKey not present");
            return null;
        }

        if (!connectionData.gameId.equals(params.gameId)) {
            logger.info("gameId invalid");
            return null;
        }

        int sessionId = createNewSessionId();

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
                logger.info("invalid player connectionKey");
                return null;
            }

            int playerIndex = connectionData.teamsPlayersKeys.get(teamIndex).indexOf(params.connectionKey);

            // check if the key is already used
            if (teamPlayerHosts.checkConnectonKeyExists(params.connectionKey)) {
                logger.info("player connectionKey already used");
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

}
