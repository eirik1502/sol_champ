package sol_engine.graphics_module;

import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class RenderingContext {

    private long windowId;


    RenderingContext(long windowId) {
        this.windowId = windowId;
        generateGlContext();
        setContextSettings();
        setVsync(true);
        clear();
        swapBuffers();  // to set the background color
    }

    private void generateGlContext() {
        glfwMakeContextCurrent(windowId);
        GL.createCapabilities(); //get opengl context
    }

    private void setContextSettings() {
//        glEnable(GL_DEPTH_TEST);
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

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT);

    }

    public void swapBuffers() {
        glfwSwapBuffers(windowId);
    }
}
