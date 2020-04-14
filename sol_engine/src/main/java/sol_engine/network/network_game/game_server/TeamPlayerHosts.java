package sol_engine.network.network_game.game_server;

import com.google.common.base.Functions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sol_engine.network.communication_layer.Host;
import sol_engine.network.network_game.GameHost;
import sol_engine.utils.stream.WithIndex;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TeamPlayerHosts {
    public static class TeamPlayer {
        public int teamIndex, playerIndex;

        public TeamPlayer(int teamIndex, int playerIndex) {
            this.teamIndex = teamIndex;
            this.playerIndex = playerIndex;
        }
    }

    private final Logger logger = LoggerFactory.getLogger(TeamPlayerHosts.class);

    private List<List<GameHost>> teamPlayerHosts;  // should be of fixed size


    public TeamPlayerHosts(List<Integer> teamSizes) {
        teamPlayerHosts = teamSizes.stream()
                .map(teamSize ->
                        IntStream.range(0, teamSize)
                                .mapToObj(i -> (GameHost) null)
                                .collect(Collectors.toList())

                )
                .collect(Collectors.toList());
    }

    public TeamPlayerHosts(TeamPlayerHosts toCopy) {
        teamPlayerHosts = toCopy.teamPlayerHosts.stream()
                .map(ArrayList::new)
                .collect(Collectors.toList());
    }

    public boolean hasHost(GameHost host) {
        return getAllPlayerHosts().contains(host);
    }

    public void setHost(GameHost host) {
        teamPlayerHosts.get(host.teamIndex).set(host.playerIndex, host);
    }


    public GameHost getHost(int teamIndex, int playerIndex) {
        return teamPlayerHosts.get(teamIndex).get(playerIndex);
    }

    public GameHost getHost(TeamPlayer teamPlayer) {
        return getHost(teamPlayer.teamIndex, teamPlayer.playerIndex);
    }

    public void replaceHost(GameHost oldHost, GameHost newHost) {
        if (oldHost.teamIndex != newHost.teamIndex || oldHost.playerIndex != newHost.playerIndex) {
            throw new IllegalArgumentException("Cannot replace a host with different teamPlayer index");
        }
        setHost(newHost);
    }

    public void removeHost(GameHost gameHost) {
        teamPlayerHosts.get(gameHost.teamIndex).set(gameHost.playerIndex, null);
    }

    public TeamPlayer getTeamPlayer(GameHost host) {
        int teamIndex = IntStream.range(0, teamPlayerHosts.size())
                .filter(ti -> teamPlayerHosts.get(ti).contains(host))
                .findFirst()
                .orElse(-1);
        if (teamIndex == -1) {
            return null;
        }
        int playerIndex = IntStream.range(0, teamPlayerHosts.get(teamIndex).size())
                .filter(pi -> teamPlayerHosts.get(teamIndex).get(pi).equals(host))
                .findFirst()
                .orElse(-1);
        if (playerIndex == -1) {
            // should never happen
            return null;
        }
        return new TeamPlayer(teamIndex, playerIndex);
    }

    public boolean checkHostExists(GameHost host) {
        return getTeamPlayer(host) == null;
    }

    private Map<GameHost, TeamPlayer> getTeamPlayerOfAllHostSpots() {
        return teamPlayerHosts.stream()
                .map(WithIndex.map())
                .flatMap(teamHostsI -> teamHostsI.value.stream()
                        .map(WithIndex.map())
                        .map(playerHostI -> new TeamPlayer(teamHostsI.i, playerHostI.i))
                )
                .collect(Collectors.toMap(
                        this::getHost,
                        teamPlayer -> teamPlayer
                ));
    }

    // return null if there are no free spots
    public TeamPlayer getFreeTeamPlayer() {
        return getTeamPlayerOfAllHostSpots().entrySet().stream()
                .filter(hostTeamPlayer -> hostTeamPlayer.getKey() == null)
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }

    public Set<GameHost> getAllPlayerHosts() {
        return teamPlayerHosts.stream()
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    public boolean checkConnectonKeyExists(String connectionKey) {
        return getAllPlayerHosts().stream()
                .map(host -> host.connectionKey)
                .anyMatch(connectionKey::equals);
    }

    public boolean allPlayersPresent() {
        return teamPlayerHosts.stream().flatMap(List::stream).allMatch(Objects::nonNull);
    }

    public boolean checkTeamPlayerFree(int teamIndex, int playerIndex) {
        return teamPlayerHosts.get(teamIndex).get(playerIndex) == null;
    }

    public boolean checkTeamPlayerOccupied(int teamIndex, int playerIndex) {
        return !checkTeamPlayerFree(teamIndex, playerIndex);
    }
}
