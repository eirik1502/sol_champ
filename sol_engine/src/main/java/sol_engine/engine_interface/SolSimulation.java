package sol_engine.engine_interface;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sol_engine.core.ModuleSystemBase;
import sol_engine.ecs.listeners.SystemAddedListener;
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
        onSetupModules();  // simulation adds modules
        modulesHandler.internalSetup();

        world = new World();

        // make sure module based systems know about modules
        systemAddedListener = (sysType, sys) -> {
            if (sys instanceof ModuleSystemBase) {
                ModuleSystemBase modSys = (ModuleSystemBase) sys;
                modSys.setModulesHandler(modulesHandler);
                modSys.internalSetupEnd();
            }
        };
        world.listeners.addSystemAddedListener(systemAddedListener);

        onSetupWorld();  // components and systems are added


        isSetup = true;
    }

    public final void start() {
        if (!isSetup) {
            logger.error("Must call setup() before start()");
        } else {
            logger.info("Starting");
            modulesHandler.internalStart();
            onStart();
        }
    }

    public final void terminate() {
        if (!terminated) {
            terminated = true;
            logger.info("terminating");
            onEnd();
            modulesHandler.internalEnd();
        } else {
            logger.warn("terminated called, but already terminating");
        }
    }

    public final void step() {
        if (terminated) {
            logger.warn("calling step() after termination");
            return;
        }

        onStepStart();

        world.update();
        modulesHandler.internalUpdate();

        onStepEnd();

        if (modulesHandler.isSimulationShouldTerminate()) {
            terminate();
        }
    }

    public ModulesHandler getModulesHandler() {
        return modulesHandler;
    }

    public boolean isTerminated() {
        return terminated;
    }

    public World getWorld() {
        return world;
    }

    protected void addModule(Module m) {
        modulesHandler.addModule(m);
    }

    protected void addModules(Module... modules) {
        Stream.of(modules).forEach(this::addModule);
    }
}
