package sol_engine.ecs;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sol_engine.ecs.listeners.EntityListener;
import sol_engine.utils.collections.ImmutableListView;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class World {
    private final Logger logger = LoggerFactory.getLogger(World.class);

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
    public <T extends SystemBase> T addSystem(Class<T> systemType) {
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
    public <T extends SystemBase> T addSystemInstance(T sys) {
        Class<T> systemType = (Class<T>) sys.getClass();
        systems.put(systemType, sys);
        sys.internalSetup(this); // component families must be set here
        listeners.systemAddedListeners.forEach(l -> l.onSystemAdded(sys.getClass(), sys));
        sys.internalSetupEnd(familyHandler.getEntitiesOfFamily(sys.compFamily));
        sys.internalStart();
        return sys;
    }

    public void addSystems(Class<? extends SystemBase>... systemTypes) {
        Arrays.stream(systemTypes).filter(Objects::nonNull).forEach(this::addSystem);
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

    public Entity createEntity(String name, String className) {
        Entity e = entityClasses.get(className).instantiate(this, name);
        return e;
    }

    public Entity addEntity(String name) {
        Entity entity = createEntity(name);
        return addEntity(entity);
    }

    public Entity addEntity(String name, String className) {
        Entity e = createEntity(name, className);
        return addEntity(e);
    }

    public Entity addEntity(final Entity e) {
        listeners.entityWillBeAddedListeners.forEach(listener -> listener.onEntityWillBeAdded(e, this));
        entitiesScheduledForAdd.add(e);
        return e;
    }


    public EntityClass getEntityClass(String name) {
        return entityClasses.get(name);
    }

    public Entity removeEntity(Entity e) {
        entitiesScheduledForRemove.add(e);
        listeners.entityWillBeRemovedListeners.forEach(listener -> listener.onEntityWillBeRemoved(e, this));
        return e;
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
            if (!entities.contains(entity)) {
                familyHandler.addEntity(entity);
                entities.add(entity);
                listeners.entityAddedListeners.forEach(listener -> listener.onEntityAdded(entity, this));
            } else {
                logger.warn("Trying to add an entity that is already present. Nothing happens");
            }
        });
        entitiesScheduledForAdd.clear();
    }

    private void removeScheduledEntities() {
        entitiesScheduledForRemove.forEach(entity -> {
            familyHandler.removeEntity(entity);
            entities.remove(entity);
            listeners.entityRemovedListeners.forEach(listener -> listener.onEntityRemoved(entity, this));
        });
        entitiesScheduledForRemove.clear();
    }
}