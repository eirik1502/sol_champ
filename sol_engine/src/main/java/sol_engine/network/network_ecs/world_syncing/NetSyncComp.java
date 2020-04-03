package sol_engine.network.network_ecs.world_syncing;

import sol_engine.ecs.Component;

import java.util.HashSet;
import java.util.Set;

public class NetSyncComp extends Component {

    public Set<Class<? extends Component>> syncComponentTypes = new HashSet<>();


    public NetSyncComp() {
    }

    public NetSyncComp(Set<Class<? extends Component>> syncComponentTypes) {
        this.syncComponentTypes = syncComponentTypes;
    }
}
