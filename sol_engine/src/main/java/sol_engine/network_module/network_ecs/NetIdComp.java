package sol_engine.network_module.network_ecs;

import sol_engine.ecs.Component;

public class NetIdComp extends Component {

    public int id;

    public NetIdComp(int id) {
        this.id = id;
    }
}
