package sol_engine.graphics_module.imgui;

import imgui.ImGui;
import sol_engine.utils.mutable_primitives.MFloat;
import sol_engine.utils.mutable_primitives.MInt;
import sol_engine.utils.mutable_primitives.MString;

import static sol_engine.graphics_module.imgui.GuiCommandsUtils.*;

public class GuiCommandsInput {

    private ImGui imgui;

    public GuiCommandsInput(ImGui imgui) {
        this.imgui = imgui;
    }


    public boolean inputFloat(String label, MFloat mvalue, float step, float stepFast, String format, GuiItemStatusFlag... flags) {
        return withConvertedMFloat(mvalue, convValue -> imgui.inputFloat(label, convValue, step, stepFast, format, GuiCommandsUtils.combineGuiFlags(flags)));
    }

    public boolean inputInt(String label, MInt mvalue, int step, int stepFast, GuiItemStatusFlag... flags) {
        return withConvertedMInt(mvalue, convValue -> imgui.inputInt(label, convValue, step, stepFast, GuiCommandsUtils.combineGuiFlags(flags)));
    }

    public boolean inputText(String label, MString mvalue, GuiItemStatusFlag... flags) {
        return withConvertedMString(mvalue, convValue -> imgui.inputText(label, convValue, GuiCommandsUtils.combineGuiFlags(flags), null, null));
    }


    public float inputFloat(String label, float value, float step, float stepFast, String format, GuiItemStatusFlag... flags) {
        return withConvertedFloat(value, convValue -> imgui.inputFloat(label, convValue, step, stepFast, format, GuiCommandsUtils.combineGuiFlags(flags)));
    }

    public int inputInt(String label, int value, int step, int stepFast, GuiItemStatusFlag... flags) {
        return withConvertedInt(value, convValue -> imgui.inputInt(label, convValue, step, stepFast, GuiCommandsUtils.combineGuiFlags(flags)));
    }

    public String inputText(String label, String value, GuiItemStatusFlag... flags) {
        return withConvertedMString(value, convValue -> imgui.inputText(label, convValue, GuiCommandsUtils.combineGuiFlags(flags), null, null));
    }
}
