package sol_engine.input_module;

import java.util.ArrayList;
import java.util.List;

public class InputModuleConfig {
    public List<Class<? extends InputSourceModule>> inputSources = new ArrayList<>();


    public InputModuleConfig() {
    }

    public InputModuleConfig(Class<? extends InputSourceModule> inputSource) {
        this.inputSources.add(inputSource);
    }

    public InputModuleConfig(List<Class<? extends InputSourceModule>> inputSources) {
        this.inputSources.addAll(inputSources);
    }
}
