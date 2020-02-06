package sol_engine.input_module;

import org.joml.Vector2f;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ExternalInputSourceModule extends InputSourceModule {
    private static final Vector2f ZERO_VECTOR = new Vector2f();

    private Map<String, Boolean> triggerInputs = new HashMap<>();
    private Map<String, Float> floatInputs = new HashMap<>();
    private Map<String, Vector2f> vectorInputs = new HashMap<>();


    public ExternalInputSourceModule(ExternalInputSourceModuleConfig config) {
        this.triggerInputs.putAll(config.triggerInputLabels.stream().collect(Collectors.toMap(Function.identity(), x -> false)));
        this.floatInputs.putAll(config.floatInputLabels.stream().collect(Collectors.toMap(Function.identity(), x -> 0f)));
        this.vectorInputs.putAll(config.vectorInputLabels.stream().collect(Collectors.toMap(Function.identity(), x -> new Vector2f())));
    }

    public ExternalInputSourceModule() {
    }

    public void updateTriggerInput(String label, boolean value) {
        triggerInputs.replace(label, value);
    }

    public void updateTriggerInputs(Map<String, Boolean> inputs) {
        inputs.forEach(this::updateTriggerInput);
    }

    public void updateFloatInput(String label, float value) {
        floatInputs.replace(label, value);
    }

    public void updateFloatInputs(Map<String, Float> inputs) {
        inputs.forEach(this::updateFloatInput);
    }

    public void updateVectorInput(String label, Vector2f value) {
        vectorInputs.replace(label, value);
    }

    public void updateVectorInputs(Map<String, Vector2f> inputs) {
        inputs.forEach(this::updateVectorInput);
    }

    @Override
    public boolean checkAction(String label) {
        return triggerInputs.getOrDefault(label, false);
    }

    @Override
    public float floatInput(String label) {
        return floatInputs.getOrDefault(label, 0f);
    }

    @Override
    public Vector2f vectorInput(String label) {
        return vectorInputs.getOrDefault(label, ZERO_VECTOR);
    }

    @Override
    public void onSetup() {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onEnd() {

    }

    @Override
    public void onUpdate() {

    }
}
