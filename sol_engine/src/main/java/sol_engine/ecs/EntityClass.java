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


    public Entity instanciate(World world, String entityName) {
        Entity e = world.createEntity(entityName);

//        Method cloneMethod;
//        try {
//            cloneMethod = Object.class.getDeclaredMethod("clone");
//            cloneMethod.setAccessible(true);
//
//        } catch (NoSuchMethodException e1) {
//            e1.printStackTrace();
//            System.exit(-1);
//            return null;
//        }
//
//        baseComponents.stream()
//                .map(baseComp -> {
//                    try {
//                        return baseComp.getClass().cast(cloneMethod.invoke(baseComp));
//                    } catch (IllegalAccessException | InvocationTargetException e1) {
//                        e1.printStackTrace();
//                        return null;
//                    }
//                })
//                .filter(Objects::nonNull)
//                .forEach(comp -> e.addComponent(comp));

        baseComponents.stream()
                .map(baseComp -> baseComp.clone())
                .filter(Objects::nonNull)  // may be null if clone failes, should not happen
                .forEach(comp -> e.addComponent(comp));

        return e;
    }


}
