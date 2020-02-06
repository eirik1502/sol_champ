package sol_engine.input_module;

import org.joml.Vector2f;

public interface InputSource {

    boolean checkAction(String label);

    float floatInput(String label);

    Vector2f vectorInput(String label);

}
