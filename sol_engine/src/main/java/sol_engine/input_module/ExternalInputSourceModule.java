package sol_engine.input_module;

import org.joml.Vector2f;

import java.util.HashMap;
import java.util.Map;

public class ExternalInputSourceModule extends InputSourceModule {
    private static final Vector2f ZERO_VECTOR = new Vector2f();

    private Map<String, Boolean> triggerInputs = new HashMap<>();
    private Map<String, Float> floatInputs = new HashMap<>();


    public ExternalInputSourceModule(ExternalInputSourceModuleConfig config) {
    }

    public ExternalInputSourceModule() {
    }

    public void updateTriggerInput(String label, boolean value) {
        triggerInputs.put(label, value);
    }

    public void updateTriggerInputs(Map<String, Boolean> inputs) {
        inputs.forEach(this::updateTriggerInput);
    }

    public void updateFloatInput(String label, float value) {
        floatInputs.put(label, value);
    }

    public void updateFloatInputs(Map<String, Float> inputs) {
        inputs.forEach(this::updateFloatInput);
    }


    @Override
    public boolean checkTrigger(String label) {
        return triggerInputs.getOrDefault(label, false);
    }

    @Override
    public float floatInput(String label) {
        return floatInputs.getOrDefault(label, 0f);
    }

    @Override
    public boolean hasTrigger(String label) {
        return triggerInputs.containsKey(label);
    }

    @Override
    public boolean hasFloatInput(String label) {
        return floatInputs.containsKey(label);
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
