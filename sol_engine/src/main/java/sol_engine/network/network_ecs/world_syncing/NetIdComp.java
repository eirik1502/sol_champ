package sol_engine.network.network_ecs.world_syncing;

import sol_engine.ecs.Component;
import sol_engine.network.network_game.GameHost;

public class NetIdComp extends Component {
    public GameHost gameHost;

    public NetIdComp(GameHost gameHost) {
        this.gameHost = gameHost;
    }
}
