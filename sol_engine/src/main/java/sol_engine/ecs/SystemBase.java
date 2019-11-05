package sol_engine.ecs;

import sol_engine.utils.Function;
import sol_engine.utils.collections.ImmutableListView;

import java.util.stream.Stream;

public abstract class SystemBase {

    protected World world;
    ComponentFamily compFamily = new ComponentFamily();
    protected ImmutableListView<Entity> entities;

    abstract protected void onSetup();

    protected void onStart() {
    }

    abstract protected void onUpdate();

    protected void onEnd() {
    }

    // FOR SETUP

    @SafeVarargs
    final protected void usingComponents(Class<? extends Component>... compTypes) {
        this.compFamily = new ComponentFamily(compTypes);
    }

    // GENERAL USE

    public Stream<Entity> entitiesStream() {
        return entities.stream();
    }

    public <T extends Component> void forEachWithComponents(Class<T> compType1, Function.TwoArg<Entity, T> consumer) {
        EntitiesUtils.ForEachWithComponents(entitiesStream(), compType1, consumer);
    }

    public <T extends Component, U extends Component> void forEachWithComponents(
            Class<T> compType1, Class<U> compType2, Function.ThreeArg<Entity, T, U> consumer) {
        EntitiesUtils.ForEachWithComponents(entitiesStream(), compType1, compType2, consumer);
    }

    public <T extends Component, U extends Component, V extends Component> void forEachWithComponents(
            Class<T> compType1, Class<U> compType2, Class<V> compType3, Function.FourArg<Entity, T, U, V> consumer) {
        EntitiesUtils.ForEachWithComponents(entitiesStream(), compType1, compType2, compType3, consumer);
    }

    public <T extends Component, U extends Component, V extends Component, W extends Component> void forEachWithComponents(
            Class<T> compType1, Class<U> compType2, Class<V> compType3, Class<W> compType4,
            Function.FiveArg<Entity, T, U, V, W> consumer) {
        EntitiesUtils.ForEachWithComponents(entitiesStream(), compType1, compType2, compType3, compType4, consumer);
    }

    public <T extends Component, U extends Component, V extends Component, W extends Component, X extends Component> void forEachWithComponents(
            Class<T> compType1, Class<U> compType2, Class<V> compType3, Class<W> compType4, Class<X> compType5,
            Function.SixArg<Entity, T, U, V, W, X> consumer) {
        EntitiesUtils.ForEachWithComponents(
                entitiesStream(), compType1, compType2, compType3, compType4, compType5, consumer);
    }

    // INTERNALS

    void internalSetup() {
        onSetup();
    }

    // must be protected as of now, as ModuleSystemBase uses this
    protected void internalStart(World world, ImmutableListView<Entity> entitiesOfFamily) {
        this.world = world;
        this.entities = entitiesOfFamily;
        onStart();
    }

    void internalUpdate() {
        this.onUpdate();
    }

    void internalEnd() {
        this.onEnd();
    }

    public ComponentFamily getCompFamily() {
        return compFamily;
    }
}
