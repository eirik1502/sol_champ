package sol_engine.ecs;

public interface SystemAddedListener {

    void onSystemAdded(Class<? extends SystemBase> systemType, SystemBase system);
}
