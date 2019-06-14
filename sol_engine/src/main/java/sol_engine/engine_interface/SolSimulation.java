package sol_engine.engine_interface;

import sol_engine.ecs.World;
import sol_engine.modules.Module;
import sol_engine.modules.ModulesHandler;

import java.util.stream.Stream;

public abstract class SolSimulation {


    protected World world;
    protected ModulesHandler modules;

    private boolean terminated = false;


    protected abstract void onStart();
    protected void onEnd() {}
    protected void onStepStart() {}
    protected void onStepEnd() {}


    public final void start() {
        modules = new ModulesHandler();
        world = new World(modules);
        onStart();
    }

    public final void terminate() {
        if (terminated) return;

        terminated = true;
        onEnd();
    }

    public final void step() {
        onStepStart();

        world.update();

        if (modules != null) {
            modules.stream().forEach(Module::onUpdate);
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
        modules.addModule(m);
    }
    protected void addModules(Module...modules) {
        Stream.of(modules).forEach(this::addModule);
    }
}
