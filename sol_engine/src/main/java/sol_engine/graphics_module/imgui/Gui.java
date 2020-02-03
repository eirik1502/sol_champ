package sol_engine.graphics_module.imgui;

import imgui.ImGui;
import imgui.classes.Context;
import imgui.classes.IO;
import imgui.impl.gl.ImplGL3;
import imgui.impl.glfw.ImplGlfw;
import imgui.internal.DrawData;
import sol_engine.graphics_module.Window;
import uno.glfw.GlfwWindow;


public class Gui {

    public static interface DrawFunc {
        void draw(GuiCommands gui);
    }

    public ImGui imgui = ImGui.INSTANCE;
    private Context ctx;
    private ImplGlfw implGlfw;
    private ImplGL3 implGl3;
    public IO io;
    private GuiCommands guiCommands;

    public float scroll = 0;

    public Gui(Window window) {
        ctx = new Context();
        imgui.styleColorsDark(null);
        GlfwWindow unoWindow = GlfwWindow.from(window.getNativeWindowId());
        implGlfw = new ImplGlfw(unoWindow, false);
        implGl3 = new ImplGL3();
        io = imgui.getIo();
        guiCommands = new GuiCommands(imgui);
    }

    public void terminate() {
        implGlfw.shutdown();
        implGl3.shutdown();
        ctx.destroy();
    }

    public void startFrame() {

        implGl3.newFrame();
        implGlfw.newFrame();
        io.setMouseWheel(scroll);
        scroll = 0;
        imgui.newFrame();
    }

    public void draw(DrawFunc func) {
        func.draw(guiCommands);
    }

    public void render() {
        imgui.render();
        DrawData drawData = imgui.getDrawData();
        if (drawData != null) {
            implGl3.renderDrawData(drawData);
        }
    }
}
