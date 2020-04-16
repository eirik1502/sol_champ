package sol_engine.input_module;

import org.joml.Vector2f;

public interface InputSource {

    boolean checkTrigger(String label);

    float floatInput(String label);

    boolean hasTrigger(String label);

    boolean hasFloatInput(String label);

}
