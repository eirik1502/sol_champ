package sol_engine.ecs.listeners;

import sol_engine.ecs.SystemBase;

public interface SystemAddedListener {

    void onSystemAdded(Class<? extends SystemBase> systemType, SystemBase system);
}
