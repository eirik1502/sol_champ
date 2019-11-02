package sol_engine.ecs;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Entity {

    public final String name;

    private World world;

    /**
     * Component container for this entity
     */
    private Map<Class<? extends Component>, Component> comps = new HashMap<>();


    Entity(World world, String name) {
        this.world = world;
        this.name = name;
    }


    public ComponentFamily getComponentTypeGroup() {
        return new ComponentFamily(comps.keySet());
    }

    Map<Class<? extends Component>, Component> getComponents() {
        return comps;
    }


    public Entity addComponent(Component comp) {
        this.comps.put(comp.getClass(), comp);
        return this;
    }

    public String getName() {
        return name;
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

}
