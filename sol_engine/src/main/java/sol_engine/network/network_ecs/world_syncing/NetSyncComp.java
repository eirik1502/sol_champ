package sol_engine.network.network_ecs.world_syncing;

import sol_engine.ecs.Component;

import java.util.HashSet;
import java.util.Set;

public class NetSyncComp extends Component {

    public boolean syncAdd = true;
    public boolean syncRemove = true;
    public Set<Class<? extends Component>> syncComponentTypes = new HashSet<>();
    public Set<Class<? extends Component>> createComponentTypesOnAdd = new HashSet<>();


    public NetSyncComp() {
    }

    public NetSyncComp(Set<Class<? extends Component>> syncComponentTypes) {
        this.syncComponentTypes = syncComponentTypes;
    }

    public NetSyncComp(boolean syncAdd, boolean syncRemove) {
        this.syncAdd = syncAdd;
        this.syncRemove = syncRemove;
    }

    public NetSyncComp(boolean syncAdd, boolean syncRemove, Set<Class<? extends Component>> syncComponentTypes) {
        this.syncAdd = syncAdd;
        this.syncRemove = syncRemove;
        this.syncComponentTypes = syncComponentTypes;
    }
}
