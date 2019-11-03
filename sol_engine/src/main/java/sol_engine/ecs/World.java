package sol_engine.ecs;


import com.google.gson.Gson;
import sol_engine.utils.collections.ImmutableListView;
import sol_engine.utils.collections.SetUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class World {

    private static List<Entity> EMPTY_ENTITIES_LIST = new ArrayList<>();
    private static ImmutableListView<Entity> EMPTY_ENTITIES_LIST_VIEW = new ImmutableListView<>(EMPTY_ENTITIES_LIST);

    private Map<String, EntityClass> entityClasses = new HashMap<>();

    private Map<Class<? extends SystemBase>, SystemBase> systems = new LinkedHashMap<>();

    private Set<Entity> entities = new HashSet<>();
    private Map<Class<? extends Component>, Set<Entity>> entitiesWithCompType = new HashMap<>();
    private Map<ComponentFamily, List<Entity>> entitiesOfFamilies = new HashMap<>();

    private Set<Entity> entitiesScheduledForRemove = new HashSet<>();
    private Set<Entity> entitiesScheduledForAdd = new HashSet<>();


    private Set<EntityClassInstanciateListener> entityClassInstanciateListeners = new HashSet<>();
    private Set<SystemAddedListener> systemAddedListeners = new HashSet<>();
    private List<WorldUpdateListener> worldUpdateListeners = new ArrayList<>();


    public World() {
    }

    // LIFECYCLE

    public void end() {
        systems.values().forEach(SystemBase::internalEnd);
    }

    public void update() {
        worldUpdateListeners.forEach(listener -> listener.onUpdateStart(this));

        worldUpdateListeners.forEach(listener -> listener.onInternalWorkStart(this));
        addScheduledEntities();
        removeScheduledEntities();
        worldUpdateListeners.forEach(listener -> listener.onInternalWorkEnd(this));

        systems.values().forEach(system -> {
            worldUpdateListeners.forEach(listener -> listener.onSystemUpdateStart(this, system));
            system.internalUpdate();
            worldUpdateListeners.forEach(listener -> listener.onSystemUpdateEnd(this, system));
        });

        worldUpdateListeners.forEach(listener -> listener.onUpdateEnd(this));
    }

    // LISTENERS

    public void addEntityClassInstanciateListener(EntityClassInstanciateListener listener) {
        entityClassInstanciateListeners.add(listener);
    }

    public void removeEntityClassInstanciateListener(EntityClassInstanciateListener listener) {
        entityClassInstanciateListeners.remove(listener);
    }

    public void addSystemAddedListener(SystemAddedListener listener) {
        systemAddedListeners.add(listener);
    }

    public void removeSystemAddedListener(SystemAddedListener listener) {
        systemAddedListeners.remove(listener);
    }

    public void addWorldUpdateListener(WorldUpdateListener listener) {
        this.worldUpdateListeners.add(listener);
    }

    public boolean removeWorldUpdateListener(WorldUpdateListener listener) {
        return this.worldUpdateListeners.remove(listener);
    }

    // SETUP

    public void addEntityClass(EntityClass entityClass) {
        entityClasses.put(entityClass.className, entityClass);
    }

    // GENERAL USE

    @SuppressWarnings("unchecked")
    public <T extends SystemBase> Class<T> addSystem(Class<T> systemType) {
        try {
            Constructor<T> constructor = systemType.getConstructor();
            T sys = constructor.newInstance();
//            systems.add(sys);
//
//            systemAddedListeners.forEach(l -> l.onSystemAdded(systemType, sys));
//
//            sys.internalStart(this);
//            return (T) sys;
            return addSystemInstance(sys);

        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            EcsLogger.logger.severe("System creation failed." +
                    "\n\tFor system: " + systemType.getName() +
                    "\n\tComponentSystems should have a no-arg constructor, " +
                    "and be a global class or a public static inner class.");
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends SystemBase> Class<T> addSystemInstance(T sys) {
        Class<T> systemType = (Class<T>) sys.getClass();
        systems.put(systemType, sys);
        systemAddedListeners.forEach(l -> l.onSystemAdded(sys.getClass(), sys));
        sys.internalSetup();
        sys.internalStart(this);
        return systemType;
    }

    @SuppressWarnings("unchecked")
    public <T extends SystemBase> T removeSystem(Class<T> systemType) {
        return (T) systems.remove(systemType);
    }

    public Entity createEntity(String name) {
        Entity e = new Entity(this, name);
        return e;
    }

    public Entity instanciateEntityClass(String className, String name) {
        Entity e = entityClasses.get(className).instanciate(this, name);
        addEntity(e);

        // call listeners
        entityClassInstanciateListeners.forEach(l -> l.onEntityClassInstanciated(className, e));
        return e;
    }

    public void addEntity(final Entity e) {
        entitiesScheduledForAdd.add(e);
    }

    public EntityClass getEntityClass(String name) {
        return entityClasses.get(name);
    }

    public void removeEntity(Entity e) {
        entitiesScheduledForRemove.add(e);
    }

    public void removeEntityByName(String name) {
        Entity e = getEntityByName(name);
        removeEntity(e);
    }

    public Set<Entity> getEntitiesByName(String name) {
        return entities.stream().filter(e -> e.name.equals(name)).collect(Collectors.toSet());
    }

    public Entity getEntityByName(String name) {
        return entities.stream().filter(e -> e.name.equals(name)).findFirst().orElseGet(() -> {
            EcsLogger.logger.severe("Trying to get an entity by name that is not present.\n\tEntity name: " + name);
            return null;
        });
    }

    public void addScheduledEntities() {
        entitiesScheduledForAdd.forEach(entity -> {
            // add the entity to component types mapping
            entity.getComponents().forEach((compType, comp) ->
                    entitiesWithCompType.computeIfAbsent(compType, key -> new HashSet<>()).add(entity)
            );

            // onUpdate entity groups
            final ComponentFamily newEntityGroup = entity.getComponentTypeGroup();
            entitiesOfFamilies.entrySet().stream()
                    // filter groups that match the new entity
                    .filter(group -> newEntityGroup.contains(group.getKey()))
                    // add the new entity to the relevant groups
                    .forEach(existingEntityGroup -> existingEntityGroup.getValue().add(entity));

            // add entity to the list of all entities
            entities.add(entity);
        });

        entitiesScheduledForAdd.clear();
    }

    public void removeScheduledEntities() {
        entitiesScheduledForRemove.forEach(e -> {

            // remove the entity to component types mapping
            e.getComponents().forEach((compType, comp) -> {
                entitiesWithCompType.get(compType).remove(e);
            });

            // onUpdate entity groups
            final ComponentFamily newEntityGroup = e.getComponentTypeGroup();

//            Iterator<Map.Entry<ComponentTypeGroup, List<sol_engine.ecs.Entity>>> it = groupEntities.entrySet().iterator();
//            while( it.hasNext() ) {
//                Map.Entry<ComponentTypeGroup, List<sol_engine.ecs.Entity>> entityGroup = it.next();
//                // remove elements
//                if (newEntityGroup.contains(entityGroup.getKey())) {
//                    it.remove();
//                }
//            }
            entitiesOfFamilies.entrySet().stream()
                    // filter groups that match the entity
                    .filter(group -> newEntityGroup.contains(group.getKey()))
                    // remove the new entity from the relevant groups
                    .forEach(group -> group.getValue().remove(e));

            // remove entities from the list of all entities
            entities.remove(e);

        });

        entitiesScheduledForRemove.clear();
    }

    public ImmutableListView<Entity> getEntitiesOfFamily(ComponentFamily compFamily) {
        List<Entity> entityGroupList = entitiesOfFamilies.computeIfAbsent(compFamily, newCompFamily -> {

            // find all entities associated with the comp group
            Set<Set<Entity>> entitiesInGroups = newCompFamily.stream()
                    .map(entitiesWithCompType::get)
                    .collect(Collectors.toSet());

            // if some comp types weren't present, there are no entities that matches the group
            if (entitiesInGroups.isEmpty() || entitiesInGroups.contains(null)) {
                return new ArrayList<>();
            }

            // take the intersection of entities by comp
            final Set<Entity> entitiesInGroup = SetUtils.intersection(entitiesInGroups);

            return new ArrayList<>(entitiesInGroup);
        });

        return new ImmutableListView<>(entityGroupList);
    }

    public List<SystemBase> getSystems() {
        return new ArrayList<>(systems.values());
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
            sb.append(gson.toJson(cs.getCompFamily())).append('\n');
        });
        sb.append("---Entities---\n");
        getEntities().forEach(e -> {
            sb.append(e.name).append(' ');
            sb.append(gson.toJson(e.getComponents().values())).append('\n');
        });
        return sb.toString();
    }
}