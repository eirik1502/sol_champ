package sol_engine.input_module;

import sol_engine.core.ModuleSystemBase;

public class InputSystem extends ModuleSystemBase {
    private static final String INPUT_GROUP_DELIMITER = ":";

    @Override
    protected void onSetup() {
        usingComponents(InputComp.class);
        usingModules(InputModule.class);
    }

    @Override
    protected void onUpdate() {
        InputModule inpModule = getModule(InputModule.class);
        forEachWithComponents(InputComp.class, (entity, inputComp) -> {
            String inputGroup = inputComp.inputGroup;
            String inputLabelPrefix = inputGroup.isEmpty() ? "" : inputGroup + INPUT_GROUP_DELIMITER;
            inputComp.triggers.keySet()
                    .forEach(label -> inputComp.triggers.replace(label, inpModule.checkAction(inputLabelPrefix + label)));

            inputComp.floatInputs.keySet()
                    .forEach(label -> inputComp.floatInputs.replace(label, inpModule.floatInput(inputLabelPrefix + label)));

            inputComp.vectorInputs.keySet()
                    .forEach(label -> inputComp.vectorInputs.replace(label, inpModule.vectorInput(inputLabelPrefix + label)));
        });
    }
}
