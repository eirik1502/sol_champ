package sol_engine.network.network_game.game_server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sol_engine.network.communication_layer.Host;
import sol_engine.network.network_game.GameHost;

import java.util.*;
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

    public void setHost(int teamIndex, int playerIndex, GameHost host) {
        teamPlayerHosts.get(teamIndex).set(playerIndex, host);
    }

    public void setHost(TeamPlayer teamPlayer, GameHost host) {
        setHost(teamPlayer.teamIndex, teamPlayer.playerIndex, host);
    }

    public GameHost getHost(int teamIndex, int playerIndex) {
        return teamPlayerHosts.get(teamIndex).get(playerIndex);
    }

    public GameHost getHost(TeamPlayer teamPlayer) {
        return getHost(teamPlayer.teamIndex, teamPlayer.playerIndex);
    }

    public void replaceHost(GameHost oldHost, GameHost newHost) {
        TeamPlayer oldHostTeamPlayer = getTeamPlayer(oldHost);
        setHost(oldHostTeamPlayer, newHost);
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
}
