package sol_engine.core;

import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sol_engine.ecs.Entity;
import sol_engine.ecs.SystemBase;
import sol_engine.ecs.World;
import sol_engine.module.Module;
import sol_engine.module.ModulesHandler;
import sol_engine.utils.collections.ImmutableListView;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class ModuleSystemBase extends SystemBase {
    private final Logger logger = LoggerFactory.getLogger(ModuleSystemBase.class);

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

    protected void onSetupEnd() {
    }

    public final void internalSetupEnd() {
        // check if the modules handler is present, else remove the system
        if (modulesHandler == null) {
            world.removeSystem(this.getClass());
            logger.warn("No modulesHandler attached to a ModuleSystem. Removing system." +
                    "\n\tSystem: " + this.getClass().getSimpleName()
            );
            return;
        }

        // check if all required modules are present, else remove the system
        if (!modulesHandler.hasAllModules(this.modulesToBeUsed)) {
            world.removeSystem(this.getClass());
            logger.warn("All required modules for a ModuleSystem are not present. Removing system." +
                    "\n\tSystem: " + this.getClass().getSimpleName() +
                    "\n\tRequired modules: " +
                    modulesToBeUsed.stream().map(Class::getSimpleName).collect(Collectors.joining(", ")) +
                    "\n\tMissing modules: " +
                    Sets.difference(this.modulesToBeUsed, modulesHandler.viewModuleTypes().copyToSet()).stream()
                            .map(Class::getSimpleName)
                            .collect(Collectors.joining(", "))
            );
            return;
        }

        onSetupEnd();
    }

}
