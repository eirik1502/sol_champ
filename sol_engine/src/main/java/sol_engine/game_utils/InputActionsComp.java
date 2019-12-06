package sol_engine.game_utils;

import sol_engine.ecs.Component;

import java.util.HashMap;
import java.util.Map;

public class InputActionsComp extends Component {

    // user set
    public Map<String, Integer> inputKeysLabels = new HashMap<>();
    public Map<String, Integer> inputMouseButtonsLabels = new HashMap<>();
    public boolean captureCursorPosition = false;


}
