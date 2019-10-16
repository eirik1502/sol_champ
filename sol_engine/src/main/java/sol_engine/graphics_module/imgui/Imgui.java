package sol_engine.graphics_module.imgui;

import imgui.IO;
import imgui.ImGui;
import imgui.imgui.Context;
import imgui.impl.gl.ImplGL3;
import imgui.impl.glfw.ImplGlfw;
import sol_engine.graphics_module.Window;
import uno.glfw.GlfwWindow;


public class Imgui {

    public static interface DrawFunc {
        void draw(ImGui imgui);
    }

    public ImGui imgui = ImGui.INSTANCE;
    private Context ctx;
    private ImplGlfw implGlfw;
    private ImplGL3 implGl3;
    private IO io;

    public Imgui(Window window) {
        ctx = new Context();
        imgui.styleColorsDark();
        long a = 1;
        GlfwWindow unoWindow = GlfwWindow.from(window.getNativeWindowId());
        implGlfw = new ImplGlfw(unoWindow);
        implGl3 = new ImplGL3();
        io = imgui.getIo();
    }

    public void terminate() {
        implGlfw.shutdown();
        implGl3.shutdown();
        ctx.destroy();
    }

    public void startFrame() {
        implGl3.newFrame();
        implGlfw.newFrame();
        imgui.newFrame();
    }

    public void draw(DrawFunc func) {
        func.draw(imgui);
    }

    public void render() {
        imgui.render();
        implGl3.renderDrawData(imgui.getDrawData());
    }
}
