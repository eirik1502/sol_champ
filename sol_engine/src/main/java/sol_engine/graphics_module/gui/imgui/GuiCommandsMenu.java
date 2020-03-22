package sol_engine.graphics_module.gui.imgui;

import imgui.ImGui;
import sol_engine.utils.mutable_primitives.MBoolean;

import static sol_engine.graphics_module.gui.imgui.GuiCommandsUtils.withConvertedBoolean;
import static sol_engine.graphics_module.gui.imgui.GuiCommandsUtils.withConvertedMBoolean;

public class GuiCommandsMenu {

    private ImGui imgui;

    public GuiCommandsMenu(ImGui imgui) {
        this.imgui = imgui;
    }

    public boolean beginMainMenuBar() {
        return imgui.beginMainMenuBar();
    }

    public void endMainMenuBar() {
        imgui.endMainMenuBar();
    }

    public boolean beginMenuBar() {
        return imgui.beginMenuBar();
    }

    public void endMenuBar() {
        imgui.endMenuBar();
    }

    public boolean beginMenu(String label, boolean enabled) {
        return imgui.beginMenu(label, enabled);
    }

    public void endMenu() {
        imgui.endMenu();
    }

    public boolean menuItem(String label, String shortcut, MBoolean selected, boolean enabled) {
        return withConvertedMBoolean(selected, convSelected -> imgui.menuItem(label, shortcut, convSelected, enabled));
    }

    public boolean menuItem(String label, String shortcut, boolean selected, boolean enabled) {
        return withConvertedBoolean(selected, convSelected -> imgui.menuItem(label, shortcut, convSelected, enabled));
    }
}
