package sol_engine.ecs;

import sol_engine.utils.ImmutableListView;

public abstract class SystemBase {

    protected World world;
    protected ComponentTypeGroup compGroupIdentity = new ComponentTypeGroup();
    protected ImmutableListView<Entity> groupEntities;

//    abstract public void onSetup();

    abstract public void onStart();

    abstract public void onUpdate();

    abstract public void onEnd();

    @SafeVarargs
    final protected void usingComponents(Class<? extends Component>... compTypes) {
        this.compGroupIdentity = new ComponentTypeGroup(compTypes);
    }

    private void retrieveGroupEntities() {
        groupEntities = world.getEntityGroup(compGroupIdentity);
    }

    public void internalStart(World world) {
        this.world = world;
        onStart();
        retrieveGroupEntities();
    }

    public void internalUpdate() {
        this.onUpdate();
    }

    public void internalEnd() {
        this.onEnd();
    }

    public ComponentTypeGroup getCompGroupIdentity() {
        return compGroupIdentity;
    }
}
