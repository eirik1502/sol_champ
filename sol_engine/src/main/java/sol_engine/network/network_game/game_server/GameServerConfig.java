package sol_engine.network.network_game.game_server;

import java.util.ArrayList;
import java.util.List;

public class GameServerConfig {
    public List<Integer> teamSizes = new ArrayList<>();
    public boolean allowObservers;
    public int port = -1;  // A free port will be chosen if the value is -1

    public GameServerConfig(List<Integer> teamSizes, boolean allowObservers) {
        this(-1, teamSizes, allowObservers);
    }


    public GameServerConfig(int port, List<Integer> teamSizes, boolean allowObservers) {
        this.port = port;
        this.teamSizes = teamSizes;
        this.allowObservers = allowObservers;
    }


    @Override
    public String toString() {
        return "GameServerConfig{" +
                "teamSizes=" + teamSizes +
                ", allowObservers=" + allowObservers +
                ", port=" + port +
                '}';
    }
}
