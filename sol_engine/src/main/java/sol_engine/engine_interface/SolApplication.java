package sol_engine.engine_interface;

import sol_engine.module.Module;
import sol_engine.module.ModulesHandler;

public abstract class SolApplication {

    protected SolSimulation simulation;
    protected ModulesHandler modules;

    private boolean started = false;
    private boolean terminated = false;


    protected void setSimulation(SolSimulation simulation) {
        this.simulation = simulation;
    }

    protected void addModule(Module module) {
        modules.addModule(module);
    }

    protected abstract void onStart();

    protected abstract void onEnd();

    protected abstract void onStep();


    public final void start() {
        if (started) return;
        started = true;

        onStart();
//        modulesHandler.startListening();
//        simulation.startListening(modulesHandler);
    }

    public final void terminate() {
        if (terminated) return;
        terminated = true;

        simulation.terminate();
        onEnd();
    }

    public final void step() {
        simulation.step();
        onStep();
    }
}
