package sol_engine.input_module;

import org.joml.Vector2f;

import java.util.HashMap;
import java.util.Map;

public class InputGuiSourceModuleConfig {

    public Vector2f cursorPosScaleToSize = null;
    public Map<String, Integer> triggerActionsMap = new HashMap<>();

    public InputGuiSourceModuleConfig() {
    }

    public InputGuiSourceModuleConfig(
            Vector2f cursorPosScaleToSize,
            Map<String, Integer> triggerActionsMap
    ) {
        this.cursorPosScaleToSize = cursorPosScaleToSize;
        this.triggerActionsMap = triggerActionsMap;
    }
}
