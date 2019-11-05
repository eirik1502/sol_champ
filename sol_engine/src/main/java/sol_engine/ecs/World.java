package sol_engine.ecs;


import sol_engine.utils.collections.ImmutableListView;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class World {

    private static List<Entity> EMPTY_ENTITIES_LIST = new ArrayList<>();
    private static ImmutableListView<Entity> EMPTY_ENTITIES_LIST_VIEW = new ImmutableListView<>(EMPTY_ENTITIES_LIST);

    final Map<String, EntityClass> entityClasses = new HashMap<>();

    final Map<Class<? extends SystemBase>, SystemBase> systems = new LinkedHashMap<>();

    final Set<Entity> entities = new HashSet<>();

    final Set<Entity> entitiesScheduledForRemove = new HashSet<>();
    final Set<Entity> entitiesScheduledForAdd = new HashSet<>();

    final WorldFamilyHandler familyHandler = new WorldFamilyHandler();

    public final WorldListeners listeners = new WorldListeners();
    public final WorldInsight insight = new WorldInsight(this);


    public World() {
    }

    // LIFECYCLE

    public void end() {
        systems.values().forEach(SystemBase::internalEnd);
    }

    public void update() {
        listeners.worldUpdateListeners.forEach(listener -> listener.onUpdateStart(this));

        listeners.worldUpdateListeners.forEach(listener -> listener.onInternalWorkStart(this));
        addScheduledEntities();
        removeScheduledEntities();
        listeners.worldUpdateListeners.forEach(listener -> listener.onInternalWorkEnd(this));

        systems.values().forEach(system -> {
            listeners.worldUpdateListeners.forEach(listener -> listener.onSystemUpdateStart(this, system));
            system.internalUpdate();
            listeners.worldUpdateListeners.forEach(listener -> listener.onSystemUpdateEnd(this, system));
        });

        listeners.worldUpdateListeners.forEach(listener -> listener.onUpdateEnd(this));
    }

    public void addEntityClass(EntityClass entityClass) {
        entityClasses.put(entityClass.className, entityClass);
    }


    @SuppressWarnings("unchecked")
    public <T extends SystemBase> Class<T> addSystem(Class<T> systemType) {
        try {
            Constructor<T> constructor = systemType.getConstructor();
            T sys = constructor.newInstance();
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
        listeners.systemAddedListeners.forEach(l -> l.onSystemAdded(sys.getClass(), sys));
        sys.internalSetup(); // component families must be set here
        sys.internalStart(this, familyHandler.getEntitiesOfFamily(sys.compFamily));
        return systemType;
    }

    // TODO: handle removing of systems in familyManager. It works, but system families are never removed
    @SuppressWarnings("unchecked")
    public <T extends SystemBase> T removeSystem(Class<T> systemType) {
        return (T) systems.remove(systemType);
    }

    public Entity createEntity(String name) {
        Entity entity = new Entity(this, name);
        return entity;
    }

    public Entity instanciateEntityClass(String className, String name) {
        Entity e = entityClasses.get(className).instanciate(this, name);
        addEntity(e);

        listeners.entityClassInstanciateListeners.forEach(l -> l.onEntityClassInstanciated(className, e));
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

    private void addScheduledEntities() {
        entitiesScheduledForAdd.forEach(entity -> {
            familyHandler.addEntity(entity);
            entities.add(entity);
        });
        entitiesScheduledForAdd.clear();
    }

    private void removeScheduledEntities() {
        entitiesScheduledForRemove.forEach(entity -> {
            familyHandler.removeEntity(entity);
            entities.remove(entity);
        });
        entitiesScheduledForRemove.clear();
    }
}