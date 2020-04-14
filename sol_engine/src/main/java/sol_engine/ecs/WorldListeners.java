package sol_engine.ecs;

import sol_engine.ecs.listeners.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WorldListeners {

    List<SystemAddedListener> systemAddedListeners = new ArrayList<>();
    List<SystemWillBeAddedListener> systemWillBeAddedListeners = new ArrayList<>();
    List<WorldUpdateListener> worldUpdateListeners = new ArrayList<>();
    List<EntityListener.WillBeAdded> entityWillBeAddedListeners = new ArrayList<>();
    List<EntityListener.WillBeRemoved> entityWillBeRemovedListeners = new ArrayList<>();
    List<EntityListener.Added> entityAddedListeners = new ArrayList<>();
    List<EntityListener.Removed> entityRemovedListeners = new ArrayList<>();

    private Map<Class<? extends EntityListener>, List<? extends EntityListener>> entityListenerListsByType = Map.of(
            EntityListener.WillBeAdded.class, entityWillBeAddedListeners,
            EntityListener.WillBeRemoved.class, entityWillBeRemovedListeners,
            EntityListener.Added.class, entityAddedListeners,
            EntityListener.Removed.class, entityRemovedListeners
    );

    WorldListeners() {
    }


    public void addSystemAddedListener(SystemAddedListener listener) {
        systemAddedListeners.add(listener);
    }

    public void removeSystemAddedListener(SystemAddedListener listener) {
        systemAddedListeners.remove(listener);
    }

    public void addSystemWillBeAddedListener(SystemWillBeAddedListener listener) {
        systemWillBeAddedListeners.add(listener);
    }

    public void removeSystemWillBeAddedListener(SystemWillBeAddedListener listener) {
        systemWillBeAddedListeners.remove(listener);
    }


    public void addWorldUpdateListener(WorldUpdateListener listener) {
        this.worldUpdateListeners.add(listener);
    }

    public boolean removeWorldUpdateListener(WorldUpdateListener listener) {
        return this.worldUpdateListeners.remove(listener);
    }


    @SuppressWarnings("unchecked")
    public <T extends EntityListener> void addEntityListener(Class<T> entityListenerType, T listener) {
        List<T> listenerList = (List<T>) entityListenerListsByType.get(entityListenerType);
        listenerList.add(listener);
    }

    public boolean removeEntityListener(EntityListener listener) {
        return entityListenerListsByType.get(listener.getClass()).remove(listener);
    }
}
