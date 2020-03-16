package sol_engine.network.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sol_engine.network.communication_layer.NetworkServer;
import sol_engine.network.network_utils.NetworkUtils;

import java.util.*;
import java.util.stream.Collectors;

public class PlayersConnectionCriteria implements ConnectionAcceptanceCriteria {
    private final Logger logger = LoggerFactory.getLogger(PlayersConnectionCriteria.class);

    private final String gameIdField = "gameId";
    private final String connectionKeyField = "connectionKey";
    private final String isObserverField = "isObserver";

    private String gameId;
    private Set<String> availablePlayerKeys = new HashSet<>();
    private List<String> playerKeysUsed = new ArrayList<>();
    private boolean allowObservers;
    private String observerKey;

    public PlayersConnectionCriteria(ServerConnectionData connectionData) {
        this.gameId = connectionData.gameId;
        this.availablePlayerKeys = connectionData.teamsPlayersKeys.stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
        this.allowObservers = connectionData.allowObservers;
        this.observerKey = connectionData.observerKey;
    }

    @Override
    public boolean accepted(NetworkServer server, Host host, String urlPath) {
        // parase query params
        Map<String, String> query = NetworkUtils.parseQueryParams(urlPath);

        logger.info("Connection request received with parameters: " + query);

        if (!query.containsKey(gameIdField) || !query.containsKey(connectionKeyField)) {
            logger.info(gameIdField + " and/or " + connectionKeyField + " not present");
            return false;
        }

        boolean isObserver = query.containsKey(isObserverField) && Boolean.parseBoolean(query.get(isObserverField));

        String gameId = query.get(gameIdField);
        String connectionKey = query.get(connectionKeyField);
        if (!gameId.equals(this.gameId)) {
            logger.info("gameId invalid");
            return false;
        }

        if (isObserver) {
            if (!allowObservers) {
                logger.info("observers are not allowed");
                return false;
            } else if (!connectionKey.equals(observerKey)) {
                logger.info("invalid observer connectionKey");
                return false;
            }
        } else {
            if (!availablePlayerKeys.contains(connectionKey)) {
                logger.info("invalid player connectionKey");
                return false;
            }
            if (playerKeysUsed.contains(connectionKey)) {
                logger.info("player connectionKey already used");
                return false;
            }

            playerKeysUsed.add(connectionKey);
        }

        return true;
    }
}
