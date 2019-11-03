package sol_engine.game_utils;

import org.joml.Vector2f;
import sol_engine.ecs.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * keys and mouse buttons should never have the same label
 */
public class UserInputComp extends Component {
    // user set
    public Map<String, Integer> inputKeysLabels = new HashMap<>();
    public Map<String, Integer> inputMouseButtonsLabels = new HashMap<>();
    public boolean captureCursorPosition = false;

    // system set
    public Map<String, Boolean> inputKeysPressed = new HashMap<>();
    public Map<String, Boolean> inputMouseButtonsPressed = new HashMap<>();
    public Vector2f cursorPosition = new Vector2f();


    public UserInputComp(Map<String, Integer> inputKeysLabels,
                         Map<String, Integer> inputMouseButtonsLabels,
                         boolean captureCursorPosition) {
        this.inputKeysLabels.putAll(inputKeysLabels);
        this.inputMouseButtonsLabels.putAll(inputMouseButtonsLabels);
        this.captureCursorPosition = captureCursorPosition;
    }

    public boolean checkPressed(String label) {
        if (inputKeysPressed.containsKey(label)) {
            return inputKeysPressed.get(label);
        } else if (inputMouseButtonsPressed.containsKey(label)) {
            return inputMouseButtonsPressed.get(label);
        }
        return false;
    }

    public Vector2f getCursorPosition() {
        if (!captureCursorPosition)
            throw new IllegalStateException("cannot get cursor position, captureCursorPosition in not set");

        return cursorPosition;
    }
}
