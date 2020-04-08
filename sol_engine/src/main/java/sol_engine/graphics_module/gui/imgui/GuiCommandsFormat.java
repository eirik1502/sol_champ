package sol_engine.graphics_module.gui.imgui;

import imgui.ImGui;
import org.joml.Vector2f;

import static sol_engine.graphics_module.gui.imgui.GuiCommandsUtils.*;

public class GuiCommandsFormat {

    private ImGui imgui;

    public GuiCommandsFormat(ImGui imgui) {
        this.imgui = imgui;
    }


    public void sameLine() {
        this.sameLine(0, 0);
    }

    public void sameLine(int offsetFromStartX) {
        this.sameLine(offsetFromStartX, 0);
    }

    public void sameLine(int offsetFromStartX, int spacing) {
        imgui.sameLine(offsetFromStartX, spacing);
    }

    public void sameLine(int offsetFromStartX, float spacing) {
        imgui.sameLine(offsetFromStartX, spacing);
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

    public void pushStyleVar(GuiStyleVar styleVar, Object value) {
        imgui.pushStyleVar(styleVar.getNative(), value);
    }

    public void popStyleVar() {
        imgui.popStyleVar(1);
    }

    public void popStyleVar(int count) {
        imgui.popStyleVar(count);
    }

}
