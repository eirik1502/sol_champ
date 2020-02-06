package sol_engine.input_module;

public class InputModuleConfig {
    public Class<? extends InputSourceModule> inputSource;

    public InputModuleConfig(Class<? extends InputSourceModule> inputSource) {
        this.inputSource = inputSource;
    }
}
