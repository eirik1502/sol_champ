package sol_engine.ecs.listeners;

import sol_engine.ecs.Entity;

public interface EntityClassInstanciateListener {

    void onEntityClassInstanciated(String className, Entity entity);
}
