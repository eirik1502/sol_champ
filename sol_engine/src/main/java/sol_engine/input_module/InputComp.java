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
    Map<String, Vector2f> vectorInputs = new HashMap<>();

    public InputComp() {
    }

    public InputComp(
            String inputGroup,
            Set<String> triggerLabels,
            Set<String> floatInputLabels,
            Set<String> vectorInputLabels
    ) {
        this.inputGroup = inputGroup;
        this.triggers.putAll(triggerLabels.stream().collect(Collectors.toMap(Function.identity(), x -> false)));
        this.floatInputs.putAll(floatInputLabels.stream().collect(Collectors.toMap(Function.identity(), x -> 0f)));
        this.vectorInputs.putAll(vectorInputLabels.stream().collect(Collectors.toMap(Function.identity(), x -> new Vector2f())));
    }

    public InputComp(
            Set<String> triggerLabels,
            Set<String> floatInputLabels,
            Set<String> vectorInputLabels
    ) {
        this("", triggerLabels, floatInputLabels, vectorInputLabels);
    }

    @Override
    public boolean checkAction(String label) {
        return triggers.getOrDefault(label, false);
    }

    @Override
    public float floatInput(String label) {
        return floatInputs.getOrDefault(label, 0f);
    }

    @Override
    public Vector2f vectorInput(String label) {
        return vectorInputs.getOrDefault(label, ZERO_VEC);
    }

    @Override
    public void copy(Component other) {
        InputComp otherComp = (InputComp) other;
        inputGroup = otherComp.inputGroup;
        triggers = new HashMap<>(otherComp.triggers);
        floatInputs = new HashMap<>(otherComp.floatInputs);
        vectorInputs = new HashMap<>(
                otherComp.vectorInputs.entrySet().stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                entry -> new Vector2f(entry.getValue())
                        ))
        );
    }
}
