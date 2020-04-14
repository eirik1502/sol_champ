package sol_engine.ecs.listeners;

import sol_engine.ecs.SystemBase;

public interface SystemWillBeAddedListener {
    void handleSystemWillBeAdded(Class<? extends SystemBase> systemType, SystemBase system);
}
