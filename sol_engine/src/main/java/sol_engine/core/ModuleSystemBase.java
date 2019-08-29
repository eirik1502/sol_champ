package sol_engine.core;

import sol_engine.ecs.SystemBase;
import sol_engine.ecs.World;
import sol_engine.module.Module;
import sol_engine.module.ModulesHandler;

import java.util.*;

public abstract class ModuleSystemBase extends SystemBase {


    private Map<Class<? extends Module>, Module> modules = new HashMap<>();
    private ModulesHandler modulesHandler;


    @SafeVarargs
    protected final void usingModules(Class<? extends Module>... moduleTypes) {
        usingModules(new HashSet<>(Arrays.asList(moduleTypes)));
    }

    protected final void usingModules(Set<Class<? extends Module>> moduleTypes) {
        moduleTypes.forEach(mt -> modules.put(mt, null));

        // check if the requested modulesHandler are present, else remove this system
        if (modulesHandler == null) {
//            world.removeSystem(this);
            System.err.println("No modulesHandler handler attached");
            return;
        }

        this.modules.keySet().forEach(mt -> {
            // get module if it is present
            Module m = modulesHandler.getModule(mt);
            if (m != null) {
                this.modules.put(mt, modulesHandler.getModule(mt));
            }
        });

        // if there are requested modulesHandler not given, remove this system
        if (this.modules.values().contains(null)) {
//            world.removeSystem(this);
            System.err.println("Moudles handler did not have the required modulesHandler for: " + getClass().getSimpleName());
            return;
        }
    }

    @SuppressWarnings("unchecked")
    protected final <T extends Module> T getModule(Class<T> moduleType) {
        return (T) modules.get(moduleType);
    }

    public void setModulesHandler(ModulesHandler modulesHandler) {
        this.modulesHandler = modulesHandler;
    }

    @Override
    public void internalStart(World world) {
        super.internalStart(world);

    }

}
