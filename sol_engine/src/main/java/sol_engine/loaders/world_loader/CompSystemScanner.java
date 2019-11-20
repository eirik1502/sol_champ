package sol_engine.loaders.world_loader;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import sol_engine.ecs.Component;
import sol_engine.ecs.SystemBase;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class CompSystemScanner {

    private Map<String, Class<? extends Component>> compTypesBySimpleName = new HashMap<>();
    private Map<String, Class<? extends SystemBase>> systemTypesBySimpleName = new HashMap<>();

    private Map<String, Reflections> reflectionsByRootPackage = new HashMap<>();

    public CompSystemScanner() {
    }

    public boolean addPackage(String packagePath) {
        if (reflectionsByRootPackage.containsKey(packagePath)) return false;

        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(packagePath))
                .setScanners(new SubTypesScanner())
        );
        reflections.getSubTypesOf(Component.class).forEach(compType ->
                compTypesBySimpleName.computeIfAbsent(compType.getSimpleName(), key -> compType));

        reflections.getSubTypesOf(SystemBase.class).forEach(systemType ->
                systemTypesBySimpleName.computeIfAbsent(systemType.getSimpleName(), key -> systemType));

        reflectionsByRootPackage.put(packagePath, reflections);
        return true;
    }

    public Class<? extends Component> getCompClassBySimpleName(String simpleName) {
        return compTypesBySimpleName.get(simpleName);
    }

    public Class<? extends SystemBase> getSystemClassBySimpleName(String simpleName) {
        return systemTypesBySimpleName.get(simpleName);
    }

//    public void updatePackage(String packagePath) {
//
//    }

    public String toString() {
        return "Scanned components:\n\t" + compTypesBySimpleName.entrySet().stream()
                .map(entry -> entry.getKey() + " \t(" + entry.getValue() + ")")
                .collect(Collectors.joining("\n\t")) +
                "\n" +
                "Scanned systems:\n\t" + systemTypesBySimpleName.entrySet().stream()
                .map(entry -> entry.getKey() + " \t(" + entry.getValue() + ")")
                .collect(Collectors.joining("\n\t"));
    }
}
