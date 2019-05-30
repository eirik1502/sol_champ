package sol_engine.app_interface;

import sol_engine.ecs.World;

public abstract class SolApplication {

    private boolean running;
    protected World world;


    protected abstract void onStart();

    protected void onEnd() {

    }

    protected void onStep() {

    }

    public final void start() {

    }

    public final void terminate() {

    }

    public final void step() {

    }
}
