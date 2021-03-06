package sol_engine.graphics_module;

import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class RenderingContext {

    private Window window;


    RenderingContext(Window window, boolean vsync) {
        this.window = window;
        generateGlContext();
        setContextSettings();
        setVsync(vsync);
        clear();
        swapBuffers();  // to set the background color
    }

    RenderingContext(Window window) {
        this(window, true);
    }

    private void generateGlContext() {
        glfwMakeContextCurrent(window.getNativeWindowId());
        //get opengl context
        GL.createCapabilities();
    }

    private void setContextSettings() {
        glEnable(GL_DEPTH_TEST);
//        glActiveTexture(GL_TEXTURE0);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        //glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public void printGlVersion() {
        System.out.println("OpenGL: " + glGetString(GL_VERSION));
    }

    public void setVsync(boolean val) {
        glfwSwapInterval(val ? 1 : 0);// Enable v-sync
    }

    public Window getWindow() {
        return window;
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

    }

    public void swapBuffers() {
        glfwSwapBuffers(window.getNativeWindowId());
    }
}
