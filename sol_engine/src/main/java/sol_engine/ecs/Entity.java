package sol_engine.ecs;

import sol_engine.utils.Function;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class Entity {
    public final String name;
    public final String className;  // null if no class was used to create this entity

    private World world;

    /**
     * Component container for this entity
     */
    private Map<Class<? extends Component>, Component> comps = new HashMap<>();


    Entity(World world, String name) {
        this(world, name, null);
    }

    Entity(World world, String name, String className) {
        this.world = world;
        this.name = name;
        this.className = className;
    }


    public ComponentFamily getComponentTypeGroup() {
        return new ComponentFamily(comps.keySet());
    }

    Map<Class<? extends Component>, Component> getComponents() {
        return comps;
    }


    // TODO: This will not work after the entity is added
    public Entity addComponent(Component comp) {
        this.comps.put(comp.getClass(), comp);
        return this;
    }

    public Entity addComponents(Component... comps) {
        Arrays.stream(comps).forEach(this::addComponent);
        return this;
    }

    public <T extends Component> Entity addComponentIfAbsent(Class<T> compType, Function.NoArgReturn<T> producer) {
        return addComponentIfAbsent(compType, producer, (comp) -> {
        });
    }

    public <T extends Component> Entity addComponentIfAbsent(
            Class<T> compType,
            Function.NoArgReturn<T> producer,
            Consumer<T> modifyFunc
    ) {
        if (!hasComponent(compType)) {
            addComponent(producer.invoke());
        }
        modifyComponent(compType, modifyFunc);
        return this;
    }

    public String getName() {
        return name;
    }

    public boolean hasComponents(Set<Class<? extends Component>> compTypes) {
        return compTypes.stream().allMatch(this::hasComponent);
    }

    public boolean hasComponent(Class<? extends Component> compType) {
        return comps.containsKey(compType);
    }

    @SuppressWarnings("unchecked")
    public <T extends Component> T getComponent(Class<T> compType) {
        return (T) comps.get(compType);
    }

    public <T extends Component> Entity modifyComponent(Class<T> compType, Consumer<T> apply) {
        apply.accept(getComponent(compType));
        return this;
    }

    public <T extends Component> Entity modifyComponent(Class<T> compType, Function.TwoArg<T, Entity> apply) {
        apply.invoke(getComponent(compType), this);
        return this;
    }

    public <T extends Component> Entity modifyIfHasComponent(Class<T> compType, Consumer<T> apply) {
        if (hasComponent(compType)) modifyComponent(compType, apply);
        return this;
    }

    public <T extends Component> Entity modifyIfHasComponent(Class<T> compType, Function.TwoArg<T, Entity> apply) {
        if (hasComponent(compType)) modifyComponent(compType, apply);
        return this;
    }

}
