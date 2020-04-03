package sol_engine.ecs.listeners;

import sol_engine.ecs.SystemBase;
import sol_engine.ecs.World;

public interface WorldUpdateListener {

    void onUpdateStart(World world);

    void onUpdateEnd(World world);

    void onInternalWorkStart(World world);

    void onInternalWorkEnd(World world);

    void onSystemUpdateStart(World world, SystemBase system);

    void onSystemUpdateEnd(World world, SystemBase system);

}
