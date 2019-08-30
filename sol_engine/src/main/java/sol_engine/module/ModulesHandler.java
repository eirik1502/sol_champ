package sol_engine.module;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class ModulesHandler {


    private Map<Class<? extends Module>, Module> modules = new HashMap<>();


    public void internalStart() {
        stream().forEach(m -> m.internalStart(this));
    }

    public void internalUpdate() {
        stream().forEach(m -> m.internalUpdate());
    }

    public void internalEnd() {
        stream().forEach(m -> m.internalEnd());
    }

    public void addModule(Module m) {
        this.modules.put(m.getClass(), m);
    }

    @SuppressWarnings("unchecked")
    public <T extends Module> T getModule(Class<T> moduleType) {
        return (T) modules.get(moduleType);
    }

    public Stream<Module> stream() {
        return modules.values().stream();
    }


}
