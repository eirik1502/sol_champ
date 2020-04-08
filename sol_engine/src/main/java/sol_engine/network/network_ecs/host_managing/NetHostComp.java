package sol_engine.network.network_ecs.host_managing;

import sol_engine.ecs.Component;
import sol_engine.network.network_game.GameHost;

public class NetHostComp extends Component {

    public GameHost host;


    public NetHostComp() {
    }

    public NetHostComp(GameHost host) {
        this.host = host;
    }
}
