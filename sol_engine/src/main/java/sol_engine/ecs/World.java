package sol_engine.ecs;


import com.google.gson.Gson;
import sol_engine.core.ModuleSystemBase;
import sol_engine.modules.Module;
import sol_engine.modules.ModulesHandler;
import sol_engine.utils.ImmutableListView;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class World {

    private Map<String, EntityClass> entityClasses = new HashMap<>();

    private List<SystemBase> systems = new ArrayList<>();

    private Set<Entity> entities = new HashSet<>();
    private Map<Class<? extends Component>, Set<Entity>> entitiesWithCompType = new HashMap<>();
    private Map<ComponentTypeGroup, List<Entity>> entityGroups = new HashMap<>();

    private Set<Entity> entitiesScheduledForRemove = new HashSet<>();

    private ModulesHandler modulesHandler;

    public World() {
        this(null);
    }
    public World(ModulesHandler modules) {
        this.modulesHandler = modules;
    }

//    public void onStart() {
//        systems.forEach(s -> s.internalStart(this));
//    }
    public void end() {
        systems.forEach(SystemBase::internalEnd);
    }
    public void update() {
        systems.forEach(SystemBase::internalUpdate);
        removeScheduledEntities();
        modulesHandler.stream().forEach(Module::onUpdate);
    }

    public void addEntityClass(EntityClass entityClass) {
        entityClasses.put(entityClass.className, entityClass);
    }

    @SuppressWarnings("unchecked")
    public <T extends SystemBase> T addSystem(Class<T> systemType) {
        try {
            Constructor<? extends SystemBase> constructor = systemType.getConstructor();
            SystemBase sys = constructor.newInstance();
            systems.add(sys);

            if (sys instanceof ModuleSystemBase) {
                ((ModuleSystemBase)sys).setModulesHandler(modulesHandler);
            }

            sys.internalStart(this);
            return (T)sys;

        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            System.err.println("SystemBase creation failed for system: "+systemType.getName()
                    +". ComponentSystems should have one no-arg constructor, and not be an inner class (may be a public static inner class).");
            e.printStackTrace();
            return null;
        }
    }


    public Entity createEntity(String name) {
        Entity e = new Entity(this, name);
        return e;
    }

    public Entity instanciateEntityClass(String className, String name) {
        Entity e = entityClasses.get(className).instanciate(this, name);
        addEntity(e);
        return e;
    }

    public void addEntity(final Entity e) {

        // add the entity to component types mapping
        e.getComponents().forEach((compType, comp) ->
                        entitiesWithCompType.computeIfAbsent(compType, key -> new HashSet<>()).add(e)
                );

        // onUpdate entity groups
        final ComponentTypeGroup newEntityGroup = e.getComponentTypeGroup();
        entityGroups.entrySet().stream()
                // filter groups that match the new entity
                .filter(group -> newEntityGroup.contains(group.getKey()))
                // add the new wntity to the relevant groups
                .forEach(existingEntityGroup -> existingEntityGroup.getValue().add(e));

        // add entity to the list of all entities
        entities.add(e);
    }

    public EntityClass getEntityClass(String name) {
        return entityClasses.get(name);
    }

    public void removeEntity(Entity e) {
        entitiesScheduledForRemove.add(e);
    }

    public Entity getEntityByName(String name) {
        return entities.stream().filter(e -> e.name.equals(name)).findFirst().orElse(null);
    }

    public void removeScheduledEntities() {
        entitiesScheduledForRemove.forEach(e -> {

            // remove the entity to component types mapping
            e.getComponents().forEach((compType, comp) -> {
                entitiesWithCompType.get(compType).remove(e);
            });

            // onUpdate entity groups
            final ComponentTypeGroup newEntityGroup = e.getComponentTypeGroup();

//            Iterator<Map.Entry<ComponentTypeGroup, List<sol_engine.ecs.Entity>>> it = groupEntities.entrySet().iterator();
//            while( it.hasNext() ) {
//                Map.Entry<ComponentTypeGroup, List<sol_engine.ecs.Entity>> entityGroup = it.next();
//                // remove elements
//                if (newEntityGroup.contains(entityGroup.getKey())) {
//                    it.remove();
//                }
//            }
            entityGroups.entrySet().stream()
                    // filter groups that match the entity
                    .filter(group -> newEntityGroup.contains(group.getKey()))
                    // remove the new entity from the relevant groups
                    .forEach(group -> group.getValue().remove(e));

            // remove entities from the list of all entities
            entities.remove(e);

        });

        entitiesScheduledForRemove.clear();
    }

    public ImmutableListView<Entity> getEntityGroup(ComponentTypeGroup compGroupsIdentity) {
        List<Entity> entityGroupList = entityGroups.computeIfAbsent(compGroupsIdentity, key -> {

            // find all entities associated with the comp group
            List<Set<Entity>> entitiesInGroups = key.stream()
                    .map(entitiesWithCompType::get)
                    .collect(Collectors.toList());

            // if some comp types wasn't present, there are no entities that matches the group
            if (entitiesInGroups.isEmpty() || entitiesInGroups.contains(null)) {
                return new ArrayList<>();
            }

            // take the intersection of entities by comp
            final Set<Entity> entitiesInGroup = new HashSet<>(entitiesInGroups.get(0));
            entitiesInGroups.stream()
                    .skip(1)
                    .forEach(entitiesInGroup::retainAll);

            return new ArrayList<>(entitiesInGroup);

        });

        return new ImmutableListView<>(entityGroupList);
    }

    public List<SystemBase> getSystems() {
        return systems;
    }
    public List<Class<? extends SystemBase>> getSystemTypes() {
        return getSystems().stream().map(SystemBase::getClass).collect(Collectors.toList());
    }

    public Set<Entity> getEntities() {
        return entities;
    }

    public Map<String, EntityClass> getEntityClasses() {
        return entityClasses;
    }

    public String toString() {
        Gson gson = new Gson();
        StringBuilder sb = new StringBuilder();
        sb.append("---Entity classes---\n");
        getEntityClasses().values().forEach(ec -> {
            sb.append(ec.className).append(' ');
            sb.append(gson.toJson(ec.getComponentsView())).append('\n');
        });
        sb.append("---Component systems---\n");
        getSystems().forEach(cs -> {
            sb.append(cs.getClass().getSimpleName()).append(' ');
            sb.append(gson.toJson(cs.getCompGroupIdentity())).append('\n');
        });
        sb.append("---Entities---\n");
        getEntities().forEach(e -> {
            sb.append(e.name).append(' ');
            sb.append(gson.toJson(e.getComponents().values())).append('\n');
        });
        return sb.toString();
    }
}