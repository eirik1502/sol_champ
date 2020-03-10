package sol_engine.module;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public abstract class Module {
    private final Logger logger = LoggerFactory.getLogger(Module.class);

    private ModulesHandler modulesHandeler = null;
    Set<Class<? extends Module>> usingModules = new HashSet<>();
    boolean simulationShouldTerminate = false;

    abstract public void onSetup();

    abstract public void onStart();

    abstract public void onEnd();

    abstract public void onUpdate();

    @SafeVarargs
    protected final void usingModules(Class<? extends Module>... moduleTypes) {
        usingModules.addAll(Arrays.asList(moduleTypes));
    }

    final void internalSetup() {
        onSetup();
    }

    public final void internalStart(ModulesHandler modulesHandler) {
        this.modulesHandeler = modulesHandler;
        onStart();
    }

    public final void internalUpdate() {
        onUpdate();
    }

    public final void internalEnd() {
        onEnd();
    }

    public final <T extends Module> T getModule(Class<T> moduleType) {
        return this.modulesHandeler.getModule(moduleType);
    }

    public void simulationShouldTerminate() {
        simulationShouldTerminate = true;
    }
}
