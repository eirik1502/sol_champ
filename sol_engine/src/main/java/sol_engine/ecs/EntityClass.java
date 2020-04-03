package sol_engine.ecs;

import sol_engine.utils.collections.ImmutableListView;
import sol_engine.utils.collections.ImmutableSetView;

import java.util.*;

public class EntityClass {

    public final String className;
    private List<String> extendsClasses = new ArrayList<>();
    private Set<Component> baseComponents = new HashSet<>();


    public EntityClass(String name, String... extendsClasses) {
        this.className = name;
        this.addSuperClasses(Arrays.asList(extendsClasses));
    }

    public EntityClass addSuperClasses(List<String> superClasses) {
        superClasses.forEach(this::addSuperClass);
        return this;
    }

    public EntityClass addSuperClass(String entityClassName) {
        extendsClasses.add(entityClassName);
        return this;
    }

    public EntityClass addBaseComponents(Component... components) {
        return addBaseComponents(Arrays.asList(components));
    }

    public EntityClass addBaseComponents(List<Component> components) {
        components.forEach(this::addBaseComponent);
        return this;
    }

    public EntityClass addBaseComponent(Component comp) {
        baseComponents.add(comp);
        return this;
    }

    public ImmutableListView<String> getSuperclassesView() {
        return new ImmutableListView<>(extendsClasses);
    }

    public ImmutableSetView<Component> getComponentsView() {
        return new ImmutableSetView<>(baseComponents);
    }

    // TODO: use overriding classes
    public Entity instantiate(World world, String entityName) {
        Entity e = world.createEntity(entityName, className);

        baseComponents.stream()
                .map(Component::clone)
                .filter(Objects::nonNull)  // may be null if clone failes, should not happen
                .forEach(e::addComponent);

        return e;
    }


}
