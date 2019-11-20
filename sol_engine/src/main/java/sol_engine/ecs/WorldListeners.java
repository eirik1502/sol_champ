package sol_engine.ecs;

import java.util.ArrayList;
import java.util.List;

public class WorldListeners {

    List<EntityClassInstanciateListener> entityClassInstanciateListeners = new ArrayList<>();
    List<SystemAddedListener> systemAddedListeners = new ArrayList<>();
    List<WorldUpdateListener> worldUpdateListeners = new ArrayList<>();


    WorldListeners() {
    }

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
}
