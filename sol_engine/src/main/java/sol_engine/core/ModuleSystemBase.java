package sol_engine.core;

import sol_engine.ecs.SystemBase;
import sol_engine.ecs.World;
import sol_engine.module.Module;
import sol_engine.module.ModulesHandler;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class ModuleSystemBase extends SystemBase {

    private Set<Class<? extends Module>> modulesToBeUsed = new HashSet<>();
    private ModulesHandler modulesHandler;

    // FOR SETUP

    @SafeVarargs
    protected final void usingModules(Class<? extends Module>... moduleTypes) {
        usingModules(new HashSet<>(Arrays.asList(moduleTypes)));
    }

    protected final void usingModules(Set<Class<? extends Module>> moduleTypes) {
        modulesToBeUsed.addAll(moduleTypes);
    }

    protected final <T extends Module> T getModule(Class<T> moduleType) {
        return modulesHandler.getModule(moduleType);
    }

    public void setModulesHandler(ModulesHandler modulesHandler) {
        this.modulesHandler = modulesHandler;
    }

    @Override
    public void internalStart(World world) {
        // check if the modules handler is present, else remove the system
        if (modulesHandler == null) {
            world.removeSystem(this.getClass());
            System.err.println("No modulesHandler attached to a system." +
                    "\n\tSystem: " + this.getClass().getSimpleName()
            );
            return;
        }

        // check if all required modules are present, else remove the system
        if (!modulesHandler.hasAllModules(this.modulesToBeUsed)) {
            world.removeSystem(this.getClass());
            System.err.println("All required modules for a system are not present." +
                    "\n\tSystem: " + this.getClass().getSimpleName() +
                    "\n\tRequired modules: "
                    + modulesToBeUsed.stream().map(Class::getSimpleName).collect(Collectors.joining(", "))
            );
            return;
        }

        super.internalStart(world);
    }

}
