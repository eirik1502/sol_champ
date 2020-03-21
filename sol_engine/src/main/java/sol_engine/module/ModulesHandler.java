package sol_engine.module;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sol_engine.utils.collections.ImmutableSetView;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Stream;

public class ModulesHandler {
    private final Logger logger = LoggerFactory.getLogger(ModulesHandler.class);


    private boolean simulationShouldTerminate = false;

    private Map<Class<? extends Module>, Module> modules = new HashMap<>();

    public void internalSetup() {
        stream().forEach(module -> {
            logger.info("Setting up module: " + module.getClass().getSimpleName() + ", object: " + module);
            module.internalSetup(this);
        });
        new HashSet<>(modules.values()).stream()
                .filter(module -> !modules.keySet().containsAll(module.usingModules))
                .peek(module -> logger.warn("All required modules are not present for module: " + module.getClass()))
                .forEach(this::removeModule);
    }

    public void internalStart() {
        stream().forEach(module -> {
            logger.info("Starting module: " + module.getClass().getSimpleName() + ", object: " + module);
            module.internalStart();
        });
    }

    public void internalUpdate() {
        stream().forEach(m -> {
            m.internalUpdate();
            if (m.simulationShouldTerminate) {
                simulationShouldTerminate = true;
            }
        });
    }

    public void internalEnd() {
        stream().forEach(module -> {
            logger.info("Ending module: " + module.getClass().getSimpleName() + ", object: " + module);
            module.internalEnd();
        });
    }

    public void addModule(Module module) {
        Class<? extends Module> moduleType = module.getClass();
        if (this.modules.containsKey(moduleType)) {
            logger.warn("adding a module that overrides an existing module of type: " + moduleType.getSimpleName());
        }
        this.modules.put(moduleType, module);
    }

    public void removeModule(Module module) {
        logger.info("Ending and removing module: " + module.getClass().getSimpleName() + ", object: " + module);
        module.internalEnd();
        this.modules.remove(module.getClass());
    }

    private void removeModule(Class<? extends Module> moduleType) {
        removeModule(this.modules.get(moduleType));
    }

    @SuppressWarnings("unchecked")
    public <T extends Module> T getModule(Class<T> moduleType) {
        Module module = modules.get(moduleType);
        if (module == null) {
            logger.error("Trying to get a module that is not present: " + moduleType);
        }
        return (T) module;
    }

    public Map<Class<? extends Module>, Module> getAllModules() {
        return new HashMap<>(modules);
    }

    public boolean hasModule(Class<? extends Module> moduleType) {
        return modules.containsKey(moduleType);
    }

    public boolean hasAllModules(Collection<Class<? extends Module>> moduleTypes) {
        return moduleTypes.stream().allMatch(this::hasModule);
    }

    public Stream<Module> stream() {
        return modules.values().stream();
    }

    public ImmutableSetView<Class<? extends Module>> viewModuleTypes() {
        return new ImmutableSetView<>(modules.keySet());
    }

    public boolean isSimulationShouldTerminate() {
        return simulationShouldTerminate;
    }
}
