package sol_engine.network.network_ecs.host_managing;

import sol_engine.ecs.Component;

public class NetIdComp extends Component {

    public int id;


    public NetIdComp() {
    }

    public NetIdComp(int id) {
        this.id = id;
    }
}
