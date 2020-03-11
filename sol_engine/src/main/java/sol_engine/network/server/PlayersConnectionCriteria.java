package sol_engine.network.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sol_engine.network.network_utils.NetworkUtils;

import java.util.*;
import java.util.stream.Collectors;

public class PlayersConnectionCriteria implements ConnectionAcceptanceCriteria {
    private final Logger logger = LoggerFactory.getLogger(PlayersConnectionCriteria.class);

    private String gameId;
    private Set<String> availablePlayerKeys = new HashSet<>();
    private List<String> connectionKeysUsed = new ArrayList<>();

    public PlayersConnectionCriteria(ConnectionData connectionData) {
        this.gameId = connectionData.gameId;
        this.availablePlayerKeys = connectionData.teamsPlayersKeys.stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean accepted(NetworkServer server, Host host, String urlPath) {
        // parase query params
        Map<String, String> query = NetworkUtils.parseQueryParams(urlPath);

        if (!query.containsKey("gameId") || !query.containsKey("playerKey")) {
            logger.info("gameId and/or playerKey not present");
            return false;
        }

        String gameId = query.get("gameId");
        String playerKey = query.get("playerKey");
        if (!gameId.equals(this.gameId)) {
            logger.info("gameId invalid");
            return false;
        }
        if (!availablePlayerKeys.contains(playerKey)) {
            logger.info("playerKey invalid");
            return false;
        }

        return true;
    }
}
