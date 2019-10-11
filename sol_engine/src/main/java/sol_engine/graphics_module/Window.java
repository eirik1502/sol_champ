package sol_engine.graphics_module;

import org.joml.Vector2f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

    private static boolean GLFW_intied = false;

    private long windowId;
    private RenderingContext context;

    private long primaryMonitor;
    private GLFWVidMode vidmode;


    public Window(WindowConfig config) {

        initGLFW();
        storeMonitor();

        // get relative monitor size
        int width = (int) ((float) vidmode.width() * config.relWidth);
        int height = (int) ((float) vidmode.height() * config.relHeight);

        createWindow(width, height, config.title);
        centerWindow();

        createRenderingContext();
        show();
        focus();

    }

    private void storeMonitor() {
        primaryMonitor = glfwGetPrimaryMonitor();
        vidmode = glfwGetVideoMode(primaryMonitor);
    }

    private void createWindow(int width, int height, String title) {

        if (!GLFW_intied) throw new IllegalStateException("Cannot create window because GLFW is not initialized");

        windowId = glfwCreateWindow(width, height, title, NULL, NULL);

        if (windowId == NULL) {
            throw new IllegalStateException("Could not create GLFW window!");
        }
    }

    public Vector2f getWindowSize() {
        int width, height;
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            glfwGetWindowSize(windowId, pWidth, pHeight);

            width = pWidth.get(0);
            height = pHeight.get(0);
        }

        return new Vector2f(width, height);
    }

    public void setWindowPosition(Vector2f position) {
        glfwSetWindowPos(windowId, (int) position.x, (int) position.y);
    }

    private void centerWindow() {
        Vector2f screenSize = new Vector2f(vidmode.width(), vidmode.height());
        setWindowPosition(screenSize.sub(getWindowSize(), new Vector2f()).mul(0.5f));
    }

    private void createRenderingContext() {
        if (windowId == -1) throw new IllegalStateException("cannot init OpenGL before a window is created");
        context = new RenderingContext(windowId);
    }

    private void initGLFW() {
        if (GLFW_intied) return;

        // inits GLFW and GL
        boolean glfwInited = glfwInit();

        if (!glfwInited) {
            throw new IllegalStateException("Could not initialize GLFW!");
        }
        GLFWErrorCallback.createPrint(System.err).set();

        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE); // the window will not be resizable

        GLFW_intied = true;
    }

    public RenderingContext getRenderingContext() {
        return context;
    }

    public void pollEvents() {
        glfwPollEvents();
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(windowId);
    }

    public void hide() {
        glfwHideWindow(windowId);
    }

    public void show() {
        glfwShowWindow(windowId);
    }

    public void focus() {
        glfwFocusWindow(windowId);
    }

    public void minimize() {
        glfwIconifyWindow(windowId);
    }

    public void setKeyCallback(WindowEventCallback.OnKey callback) {
        glfwSetKeyCallback(windowId, (long window, int key, int scancode, int action, int mods) ->
                callback.invoke(this, key, scancode, action));
    }

    public void setMouseButtonCallback(WindowEventCallback.OnMouseButton callback) {
        glfwSetMouseButtonCallback(windowId, (long window, int button, int action, int mods) ->
                callback.invoke(this, button, action)
        );
    }

    public void setCursorPosCallback(WindowEventCallback.OnCursorPos callback) {
        glfwSetCursorPosCallback(windowId, (long window, double xPos, double yPos) ->
                callback.invoke(this, (float) xPos, (float) yPos)
        );
    }
}
