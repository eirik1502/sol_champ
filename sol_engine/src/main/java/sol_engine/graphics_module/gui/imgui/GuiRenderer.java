package sol_engine.graphics_module.gui.imgui;

import imgui.ImGui;
import imgui.classes.Context;
import imgui.classes.IO;
import imgui.impl.gl.ImplGL3;
import imgui.impl.glfw.ImplGlfw;
import imgui.internal.DrawData;
import sol_engine.graphics_module.Window;
import sol_engine.graphics_module.gui.DrawFunc;
import uno.glfw.GlfwWindow;


public class GuiRenderer {

    private ImGui imgui = ImGui.INSTANCE;
    private Context ctx;
    private ImplGlfw implGlfw;
    private ImplGL3 implGl3;
    private IO io;
    private GuiCommands guiCommands;

    private float scroll = 0;

    public GuiRenderer(Window window) {
        ctx = new Context();
        imgui.styleColorsDark(null);
        GlfwWindow unoWindow = GlfwWindow.from(window.getNativeWindowId());
        implGlfw = new ImplGlfw(unoWindow, false);
        implGl3 = new ImplGL3();
        io = imgui.getIo();
        guiCommands = new GuiCommands(imgui);
    }

    public ImGui getNativeImGui() {
        return imgui;
    }

    public IO getIO() {
        return io;
    }

    public void setScroll(float scroll) {
        this.scroll = scroll;
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
//        imgui.endFrame();
        imgui.render();
        DrawData drawData = imgui.getDrawData();
        if (drawData != null) {
            implGl3.renderDrawData(drawData);
        }
    }
}
