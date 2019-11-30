package sol_engine.graphics_module.imgui;

import imgui.ImGui;
import sol_engine.utils.mutable_primitives.MBoolean;

import static sol_engine.graphics_module.imgui.GuiCommandsUtils.withConvertedBooleanArray;
import static sol_engine.graphics_module.imgui.GuiCommandsUtils.withConvertedMBooleanArray;

public class GuiCommandsCore {


    private ImGui imgui;

    public GuiCommandsCore(ImGui imgui) {
        this.imgui = imgui;
    }


    public void text(String textFmt, Object... args) {
        imgui.text(textFmt, args);
    }

    public void labelText(String label, String textFmt, Object... args) {
        imgui.labelText(label, textFmt, args);
    }

    public boolean checkbox(String label, MBoolean mvalue, GuiItemStatusFlag... flags) {
        return withConvertedMBooleanArray(mvalue, convValue -> imgui.checkbox(label, convValue, GuiCommandsUtils.combineGuiFlags(flags)));
    }

    public boolean checkbox(String label, boolean value, GuiItemStatusFlag... flags) {
        return withConvertedBooleanArray(value, convValue -> imgui.checkbox(label, convValue, GuiCommandsUtils.combineGuiFlags(flags)));
    }
}
