package sol_engine.game_utils;

import sol_engine.core.ModuleSystemBase;
import sol_engine.input_module.InputModule;

public class UserInputSystem extends ModuleSystemBase {
    @Override
    protected void onSetup() {
        usingModules(InputModule.class);
        usingComponents(InputComp.class);
    }

    @Override
    protected void onUpdate() {
        InputModule inpModule = getModule(InputModule.class);

        forEachWithComponents(
                InputComp.class,
                (entity, userInpComp) -> {
                    userInpComp.inputKeysLabels.forEach((label, keyCode) -> {
                        userInpComp.inputKeysPressed.put(label, inpModule.keyHeld(keyCode));
                    });
                    userInpComp.inputMouseButtonsLabels.forEach((label, mouseButtonCode) -> {
                        userInpComp.inputMouseButtonsPressed.put(label, inpModule.mouseButtonHeld(mouseButtonCode));
                    });
                    if (userInpComp.captureCursorPosition) {
                        inpModule.cursorPosition(userInpComp.cursorPosition);
                    }
                }
        );
    }
}
