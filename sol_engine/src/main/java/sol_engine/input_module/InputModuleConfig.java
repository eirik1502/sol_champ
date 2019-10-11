package sol_engine.input_module;

import org.joml.Vector2f;

public class InputModuleConfig {

    public Vector2f cursorPosScaleToSize = null;

    public InputModuleConfig() {
    }

    public InputModuleConfig(Vector2f cursorPosScaleToSize) {
        this.cursorPosScaleToSize = cursorPosScaleToSize;
    }
}
