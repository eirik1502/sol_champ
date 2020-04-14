package sol_engine.network.network_game.game_server;

import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sol_engine.network.communication_layer.Host;
import sol_engine.network.communication_layer.NetworkCommunicationServer;
import sol_engine.network.communication_layer.NetworkServer;
import sol_engine.network.network_game.*;
import sol_engine.network.packet_handling.NetworkPacket;
import sol_engine.utils.collections.Pair;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ServerGameHostsManager implements NetworkCommunicationServer.PacketHandler {
    private final Logger logger = LoggerFactory.getLogger(ServerGameHostsManager.class);


    private ServerConnectionData connectionData;


    private Map<Host, GameHost> openHosts = new ConcurrentHashMap<>();  // all open hosts
    private TeamPlayerHosts teamPlayerHosts;  // open player hosts
    private Set<GameHost> observerHosts = Collections.newSetFromMap(new ConcurrentHashMap<>());  // open observer hosts

    private Deque<GameHost> newConnectedHosts = new ConcurrentLinkedDeque<>();  // newly connected hosts to be retrieved
    private Deque<GameHost> newDisconnectedHosts = new ConcurrentLinkedDeque<>();  // newly disconnected hosts to be retrieved
    private PacketsQueue inputPacketQueue = new PacketsQueue();


    public ServerGameHostsManager(ServerConnectionData connectionData) {
        this.connectionData = connectionData;
        teamPlayerHosts = new TeamPlayerHosts(
                connectionData.teamsPlayersKeys.stream()
                        .map(List::size)
                        .collect(Collectors.toList())
        );
    }

    public void addGameHost(GameHost gameHost, Host host) {
        openHosts.put(host, gameHost);
        if (gameHost.isObserver) {
            observerHosts.add(gameHost);
        } else {
            // the teamPlayer position must be checked to be free before the host is added
            teamPlayerHosts.setHost(gameHost);
        }
        logger.info("Game host added: " + gameHost);
        newConnectedHosts.add(gameHost);
    }

    public void removeHost(Host host) {
        GameHost gameHost = openHosts.remove(host);
        if (gameHost == null) {
            logger.warn("Removed host that was never added");
        } else {
            if (gameHost.isObserver) {
                observerHosts.remove(gameHost);
            } else {
                teamPlayerHosts.removeHost(gameHost);
            }
            newDisconnectedHosts.add(gameHost);
            logger.info("GameHost removed: " + gameHost);
        }
    }

    boolean checkTeamPlayerFree(GameHost gameHost) {
        return checkTeamPlayerFree(gameHost.teamIndex, gameHost.playerIndex);
    }

    boolean checkTeamPlayerFree(int teamIndex, int playerIndex) {
        return teamPlayerHosts.checkTeamPlayerFree(teamIndex, playerIndex);
    }

    @Override
    public void handlePacket(NetworkPacket packet, Host host) {
        if (openHosts.containsKey(host)) {
            GameHost gameHost = openHosts.get(host);
            if (teamPlayerHosts.hasHost(gameHost)) {
                inputPacketQueue.add(gameHost, packet);
            } else {
                logger.warn("Got a packet from an observer client, will be discarded. Game host: " + gameHost + ", packet: " + packet);
            }
        } else {
            logger.warn("Got a packet from a non-open host: " + host);
        }
    }

    public Host getOpenHost(GameHost ghost) {
        return openHosts.entrySet().stream()
                .filter(entry -> entry.getValue() == ghost)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseGet(() -> {
                    logger.warn("Trying to get a Host from a GameHost that is not an open host: " + ghost);
                    return null;
                });
    }

    public Deque<GameHost> peekNewConnectedHosts() {
        return new ArrayDeque<>(newConnectedHosts);
    }

    public synchronized Deque<GameHost> popNewConnectedHosts() {
        Deque<GameHost> holdNewConnections = peekNewConnectedHosts();
        newConnectedHosts.clear();
        return holdNewConnections;
    }

    public Deque<GameHost> peekNewDisconnectedHosts() {
        return new ArrayDeque<>(newDisconnectedHosts);
    }

    public synchronized Deque<GameHost> popNewDisconnectedHosts() {
        Deque<GameHost> holdNewDisconnections = peekNewDisconnectedHosts();
        newDisconnectedHosts.clear();
        return holdNewDisconnections;
    }

    public PacketsQueue peekInputPacketQueue() {
        return new PacketsQueue(inputPacketQueue);
    }

    public synchronized PacketsQueue popInputPacketQueue() {
        PacketsQueue holdQueue = peekInputPacketQueue();
        inputPacketQueue.clear();
        return holdQueue;
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


}
