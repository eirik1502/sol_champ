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

            inputComp.triggers.keySet().stream()
                    .map(label -> inputLabelPrefix + label)
                    .forEach(label -> inputComp.triggers.replace(label, inpModule.checkAction(label)));

            inputComp.floatInputs.keySet().stream()
                    .map(label -> inputLabelPrefix + label)
                    .forEach(label -> inputComp.floatInputs.replace(label, inpModule.floatInput(label)));

            inputComp.vectorInputs.keySet().stream()
                    .map(label -> inputLabelPrefix + label)
                    .forEach(label -> inputComp.vectorInputs.replace(label, inpModule.vectorInput(label)));
        });
    }
}
