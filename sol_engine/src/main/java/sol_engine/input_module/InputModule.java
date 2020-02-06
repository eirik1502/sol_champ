package sol_engine.input_module;

import org.joml.Vector2f;

public class InputModule extends InputSourceModule {

    private InputModuleConfig config;
    private InputSourceModule inputSourceModule;

    public boolean checkAction(String label) {
        return inputSourceModule.checkAction(label);
    }

    public float floatInput(String label) {
        return inputSourceModule.floatInput(label);
    }

    public Vector2f vectorInput(String label) {
        return inputSourceModule.vectorInput(label);
    }

    public InputModule(InputModuleConfig config) {
        this.config = config;
    }

    @Override
    public void onSetup() {
        usingModules(config.inputSource);
    }

    @Override
    public void onStart() {
        inputSourceModule = getModule(config.inputSource);
    }

    @Override
    public void onEnd() {

    }

    @Override
    public void onUpdate() {


    }
}
