package sol_engine.graphics_module.imgui;

import imgui.ImGui;
import org.joml.Vector2f;
import sol_engine.utils.mutable_primitives.MBoolean;
import sol_engine.utils.mutable_primitives.MFloat;
import sol_engine.utils.mutable_primitives.MInt;
import sol_engine.utils.mutable_primitives.MString;

import static sol_engine.graphics_module.imgui.GuiCommandsUtils.*;

public class GuiCommands {

    private ImGui imgui;


    public GuiCommands(ImGui imgui) {
        this.imgui = imgui;
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
        return imgui.menuItem(label, shortcut, selected, enabled);
    }


    public void text(String textFmt, Object... args) {
        imgui.text(textFmt, args);
    }

    public boolean collapsingHeader(String label, MBoolean open, GuiWindowFlags... flags) {
        return withConvertedMBoolean(open, convOpen -> imgui.collapsingHeader(label, convOpen, combineGuiFlags(flags)));
    }

    public boolean collapsingHeader(String label, GuiWindowFlags... flags) {
        return imgui.collapsingHeader(label, combineGuiFlags(flags));
    }

    public void setNextWindowPos(Vector2f pos, Vector2f pivot, GuiCond cond) {
        imgui.setNextWindowPos(convertVector2(pos), cond.getNativeCond(), convertVector2Two(pivot));
    }

    public void setNextWindowPos(Vector2f pos, GuiCond cond) {
        imgui.setNextWindowPos(convertVector2(pos), cond.getNativeCond(), getImguiVector2Zero());
    }

    public void setNextWindowSize(Vector2f size, GuiCond cond) {
        imgui.setNextWindowSize(convertVector2(size), cond.getNativeCond());
    }

    public void setNextWindowSizeConstraints(Vector2f sizeMin, Vector2f sizeMax) {
        imgui.setNextWindowSizeConstraints(convertVector2(sizeMin), convertVector2Two(sizeMax), null, null);
    }

    public boolean inputFloat(String label, MFloat mvalue, float step, float stepFast, String format) {
        return withConvertedMFloat(mvalue, convValue -> imgui.inputFloat(label, convValue, step, stepFast, format, 0));
    }

    public boolean inputInt(String label, MInt mvalue, int step, int stepFast) {
        return withConvertedMInt(mvalue, convValue -> imgui.inputInt(label, convValue, step, stepFast, 0));
    }

    public boolean inputText(String label, MString mvalue) {
        return withConvertedMString(mvalue, convValue -> imgui.inputText(label, convValue, 0, null, null));
    }


    public float inputFloat(String label, float value, float step, float stepFast, String format) {
        return withConvertedFloat(value, convValue -> imgui.inputFloat(label, convValue, step, stepFast, format, 0));
    }

    public int inputInt(String label, int value, int step, int stepFast) {
        return withConvertedInt(value, convValue -> imgui.inputInt(label, convValue, step, stepFast, 0));
    }

    public String inputText(String label, String value) {
        return withConvertedMString(value, convValue -> imgui.inputText(label, convValue, 0, null, null));
    }


    public boolean checkbox(String label, MBoolean mvalue) {
        return withConvertedMBoolean(mvalue, convValue -> imgui.checkbox(label, convValue));
    }

    public boolean checkbox(String label, boolean value) {
        return withConvertedBoolean(value, convValue -> imgui.checkbox(label, convValue));
    }
}
