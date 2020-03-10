package sol_engine.engine_interface;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sol_engine.core.ModuleSystemBase;
import sol_engine.ecs.SystemAddedListener;
import sol_engine.ecs.World;
import sol_engine.module.Module;
import sol_engine.module.ModulesHandler;

import java.util.stream.Stream;

public abstract class SolSimulation {
    private final Logger logger = LoggerFactory.getLogger(SolSimulation.class);

    protected World world;
    protected ModulesHandler modulesHandler;

    private SystemAddedListener systemAddedListener;
    private boolean isSetup = false;
    private boolean terminated = false;

    protected abstract void onSetupModules();

    protected abstract void onSetupWorld();

    protected void onStart() {
    }

    protected void onEnd() {
    }

    protected void onStepStart() {
    }

    protected void onStepEnd() {
    }

    public void setup() {
        if (isSetup) {
            logger.warn("Should only call setup() once");
            return;
        }
        modulesHandler = new ModulesHandler();
        onSetupModules();
        modulesHandler.internalSetup();

        world = new World();
        systemAddedListener = (sysType, sys) -> {
            if (sys instanceof ModuleSystemBase) {
                ((ModuleSystemBase) sys).setModulesHandler(modulesHandler);
            }
        };
        world.listeners.addSystemAddedListener(systemAddedListener);

        onSetupWorld();
        isSetup = true;
    }

    public final void start() {
        if (!isSetup) {
            logger.error("Must call setup() before start()");
            return;
        }
        modulesHandler.internalStart();
        onStart();
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
