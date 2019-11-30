package sol_engine.graphics_module.imgui;

import imgui.ImGui;
import sol_engine.utils.mutable_primitives.MBoolean;

import static sol_engine.graphics_module.imgui.GuiCommandsUtils.*;

public class GuiCommands {

    private ImGui imgui;

    public GuiCommandsMenu menu;
    public GuiCommandsChart chart;
    public GuiCommandsContainer container;
    public GuiCommandsFormat format;
    public GuiCommandsInput input;
    public GuiCommandsCore core;

    public GuiCommands(ImGui imgui) {
        this.imgui = imgui;
        menu = new GuiCommandsMenu(imgui);
        chart = new GuiCommandsChart(imgui);
        container = new GuiCommandsContainer(imgui);
        format = new GuiCommandsFormat(imgui);
        input = new GuiCommandsInput(imgui);
        core = new GuiCommandsCore(imgui);
    }


    public ImGui getNative() {
        return imgui;
    }

    public boolean begin(String name, MBoolean pOpen, GuiWindowFlags... flags) {
        return withConvertedMBoolean(pOpen, convPOpen -> imgui.begin(name, convPOpen, combineGuiFlags(flags)));
    }

    public boolean begin(String name, boolean pOpen, GuiWindowFlags... flags) {
        return imgui.begin(name, convertBoolean(pOpen), combineGuiFlags(flags));
    }

    public void end() {
        imgui.end();
    }

}
