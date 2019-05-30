package sol_engine.ecs;

import sol_engine.utils.ImmutableListView;

public abstract class ComponentSystem {

    protected World world;
    protected ComponentTypeGroup compGroupsIdentity = new ComponentTypeGroup();
    protected ImmutableListView<Entity> entityGroups;

    abstract public void start();
    abstract public void update();
    abstract public void end();

    @SafeVarargs
    final protected void setComponentTypes(Class<? extends Component>...compTypes) {
        this.compGroupsIdentity = new ComponentTypeGroup(compTypes);
    }

    private void storeCompGroups() {
        entityGroups = world.getEntityGroup(compGroupsIdentity);
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

    public ComponentTypeGroup getCompGroupsIdentity() {
        return compGroupsIdentity;
    }
}
