package sol_engine.graphics_module.gui.imgui;

import imgui.ImGui;
import sol_engine.utils.mutable_primitives.MBoolean;

import static sol_engine.graphics_module.gui.imgui.GuiCommandsUtils.combineGuiFlags;
import static sol_engine.graphics_module.gui.imgui.GuiCommandsUtils.withConvertedMBoolean;

public class GuiCommandsContainer {

    private ImGui imgui;

    public GuiCommandsContainer(ImGui imgui) {
        this.imgui = imgui;
    }

    public boolean collapsingHeader(String label, MBoolean open, GuiWindowFlags... flags) {
        return withConvertedMBoolean(open, convOpen -> imgui.collapsingHeader(label, convOpen, combineGuiFlags(flags)));
    }

    public boolean collapsingHeader(String label, GuiWindowFlags... flags) {
        return imgui.collapsingHeader(label, combineGuiFlags(flags));
    }

}
