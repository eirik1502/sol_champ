package sol_engine.network.network_ecs.host_managing;

import sol_engine.ecs.Component;

public class TeamPlayerComp extends Component {

    public int teamIndex;
    public int playerIndex;


    public TeamPlayerComp() {
    }

    public TeamPlayerComp(int teamIndex, int playerIndex) {
        this.teamIndex = teamIndex;
        this.playerIndex = playerIndex;
    }
}
