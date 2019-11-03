package sol_engine.ecs;

public interface WorldUpdateListener {

    void onUpdateStart(World world);

    void onUpdateEnd(World world);

    void onInternalWorkStart(World world);

    void onInternalWorkEnd(World world);

    void onSystemUpdateStart(World world, SystemBase system);

    void onSystemUpdateEnd(World world, SystemBase system);

}
