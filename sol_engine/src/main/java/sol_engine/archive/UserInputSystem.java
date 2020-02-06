package sol_engine.archive;

import sol_engine.core.ModuleSystemBase;
import sol_engine.input_module.InputGuiSourceModule;

public class UserInputSystem extends ModuleSystemBase {
    @Override
    protected void onSetup() {
        usingModules(InputGuiSourceModule.class);
        usingComponents(InputComp.class);
    }

    @Override
    protected void onUpdate() {
        InputGuiSourceModule inpModule = getModule(InputGuiSourceModule.class);

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
