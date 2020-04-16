package sol_engine.input_module;

import org.joml.Vector2f;
import sol_engine.module.Module;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class InputModule extends Module {

    private InputModuleConfig config;
    private List<InputSourceModule> inputSourceModules;

    public boolean checkAction(String label) {
        return inputSourceModules.stream()
                .filter(source -> source.hasTrigger(label))
                .findFirst()
                .orElse(inputSourceModules.get(0))
                .checkTrigger(label);
    }

    public float floatInput(String label) {
        return inputSourceModules.stream()
                .filter(source -> source.hasFloatInput(label))
                .findFirst()
                .orElse(inputSourceModules.get(0))
                .floatInput(label);
    }

    public Vector2f vectorInput(String label) {
        return new Vector2f();
    }

    public InputModule(InputModuleConfig config) {
        this.config = config;
    }

    @Override
    public void onSetup() {
        usingModules(config.inputSources.stream()
                .map(inputSource -> (Class<? extends Module>) inputSource)
                .collect(Collectors.toList())
        );
    }

    @Override
    public void onStart() {
        inputSourceModules = config.inputSources.stream()
                .map(this::getModule)
                .collect(Collectors.toList());

        if (inputSourceModules.isEmpty()) {
            throw new IllegalArgumentException("InputModule should be given at least one InputSourceModule");
        }
    }

    @Override
    public void onEnd() {

    }

    @Override
    public void onUpdate() {


    }
}
