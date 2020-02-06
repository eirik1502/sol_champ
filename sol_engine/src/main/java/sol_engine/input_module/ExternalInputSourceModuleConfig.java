package sol_engine.input_module;

import java.util.HashSet;
import java.util.Set;

public class ExternalInputSourceModuleConfig {
    public Set<String> triggerInputLabels = new HashSet<>();
    public Set<String> floatInputLabels = new HashSet<>();
    public Set<String> vectorInputLabels = new HashSet<>();

    public ExternalInputSourceModuleConfig(
            Set<String> triggerInputLabels,
            Set<String> floatInputLabels,
            Set<String> vectorInputLabels
    ) {
        this.triggerInputLabels = triggerInputLabels;
        this.floatInputLabels = floatInputLabels;
        this.vectorInputLabels = vectorInputLabels;
    }
}
