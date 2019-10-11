package sol_engine.engine_interface;

import sol_engine.core.ModuleSystemBase;
import sol_engine.ecs.SystemAddedListener;
import sol_engine.ecs.World;
import sol_engine.module.Module;
import sol_engine.module.ModulesHandler;

import java.util.stream.Stream;

public abstract class SolSimulation {


    protected World world;
    protected ModulesHandler modulesHandler;

    private SystemAddedListener systemAddedListener;
    private boolean terminated = false;

    protected abstract void setup();

    protected void onStart() {
    }

    protected void onEnd() {
    }

    protected void onStepStart() {
    }

    protected void onStepEnd() {
    }


    public final void start() {
        modulesHandler = new ModulesHandler();
        world = new World();

        systemAddedListener = (sysType, sys) -> {
            if (sys instanceof ModuleSystemBase) {
                ((ModuleSystemBase) sys).setModulesHandler(modulesHandler);
            }
        };
        world.addSystemAddedListener(systemAddedListener);

        setup();
        modulesHandler.internalStart();
    }

    public final void terminate() {
        if (terminated) return;

        terminated = true;
        onEnd();
        modulesHandler.internalEnd();
    }

    public final void step() {
        onStepStart();

        world.update();
        modulesHandler.internalUpdate();
        if (modulesHandler.isSimulationShouldTerminate()) {
            terminate();
        }

        onStepEnd();
    }

    public boolean isTerminated() {
        return terminated;
    }

    public World getGameState() {
        return world;
    }

    protected void addModule(Module m) {
        modulesHandler.addModule(m);
    }

    protected void addModules(Module... modules) {
        Stream.of(modules).forEach(this::addModule);
    }
}
