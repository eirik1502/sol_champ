package sol_engine.module;

import java.util.HashSet;
import java.util.Set;

public abstract class Module {

    private ModulesHandler modulesHandeler = null;
    private Set<Class<? extends Module>> modulesInUse = new HashSet<>();


    abstract public void onStart();

    abstract public void onEnd();

    abstract public void onUpdate();

    public final void internalStart(ModulesHandler modulesHandler) {
        this.modulesHandeler = modulesHandler;
        onStart();
    }

    public final void internalUpdate() {
        onUpdate();
    }

    public void internalEnd() {
        onEnd();
    }

    public <T extends Module> T getModule(Class<T> moduleType) {
        return this.modulesHandeler.getModule(moduleType);
    }

    public void usingModules(Class<? extends Module>... modules) {

    }
}
