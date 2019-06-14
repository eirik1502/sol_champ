package sol_engine.modules;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class ModulesHandler {

    private Map<Class<? extends Module>, Module> modules = new HashMap<>();

    public void addModule(Module m) {
        this.modules.put(m.getClass(), m);
    }

    @SuppressWarnings("unchecked")
    public <T extends Module> T getModule(Class<T> moduleType) {
        return (T)modules.get(moduleType);
    }

    public Stream<Module> stream() {
        return modules.values().stream();
    }
}
