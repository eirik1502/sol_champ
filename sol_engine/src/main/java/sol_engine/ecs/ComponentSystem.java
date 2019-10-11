package sol_engine.ecs;

import sol_engine.utils.ImmutableListView;

import java.util.ArrayList;

public abstract class ComponentSystem {

    protected World world;
    protected ComponentFamily compGroupsIdentity = new ComponentFamily();
    protected ImmutableListView<Entity> entityGroups = new ImmutableListView<>(new ArrayList<>());

    abstract public void start();

    abstract public void update();

    abstract public void end();

    @SafeVarargs
    final protected void setComponentTypes(Class<? extends Component>... compTypes) {
        this.compGroupsIdentity = new ComponentFamily(compTypes);
    }

    private void storeCompGroups() {
        entityGroups = world.getEntitiesOfFamily(compGroupsIdentity);
    }

    void internalStart(World world) {
        this.world = world;
        start();
        storeCompGroups();
    }

    void internalUpdate() {
        this.update();
    }

    void internalEnd() {
        this.end();
    }

    public ComponentFamily getCompGroupsIdentity() {
        return compGroupsIdentity;
    }
}
