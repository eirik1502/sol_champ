package sol_engine.input_module;

import org.joml.Vector2f;
import sol_engine.ecs.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class InputComp extends Component implements InputSource {
    private static Vector2f ZERO_VEC = new Vector2f();

    // Group value of "" indicates no group
    public String inputGroup = "";
    Map<String, Boolean> triggers = new HashMap<>();
    Map<String, Float> floatInputs = new HashMap<>();

    public InputComp() {
    }

    public InputComp(
            String inputGroup,
            Set<String> triggerLabels,
            Set<String> floatInputLabels
    ) {
        this.inputGroup = inputGroup;
        this.triggers.putAll(triggerLabels.stream().collect(Collectors.toMap(Function.identity(), x -> false)));
        this.floatInputs.putAll(floatInputLabels.stream().collect(Collectors.toMap(Function.identity(), x -> 0f)));
    }

    public InputComp(
            Set<String> triggerLabels,
            Set<String> floatInputLabels
    ) {
        this("", triggerLabels, floatInputLabels);
    }

    @Override
    public boolean checkTrigger(String label) {
        return triggers.getOrDefault(label, false);
    }

    @Override
    public float floatInput(String label) {
        return floatInputs.getOrDefault(label, 0f);
    }

    @Override
    public boolean hasTrigger(String label) {
        return triggers.containsKey(label);
    }

    @Override
    public boolean hasFloatInput(String label) {
        return floatInputs.containsKey(label);
    }

    @Override
    public void copy(Component other) {
        InputComp otherComp = (InputComp) other;
        inputGroup = otherComp.inputGroup;
        triggers = new HashMap<>(otherComp.triggers);
        floatInputs = new HashMap<>(otherComp.floatInputs);
    }
}
