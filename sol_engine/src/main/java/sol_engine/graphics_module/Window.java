package sol_engine.graphics_module;

import org.joml.Vector2f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWErrorCallbackI;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    private static final Logger logger = LoggerFactory.getLogger(Window.class);

    private static boolean GLFW_intied = false;
    private static int GLFW_users = 0;

    private static final Object GLFW_set_inited_lock = new Object();

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

        createRenderingContext(config.vsync);
    }

    public long getNativeWindowId() {
        return windowId;
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

    private void createRenderingContext(boolean vsync) {
        if (windowId == -1) throw new IllegalStateException("cannot init OpenGL before a window is created");
        context = new RenderingContext(this, vsync);
    }

    private static void initGLFW() {
        synchronized (GLFW_set_inited_lock) {
            if (GLFW_intied) {
                logger.info("Initing GLFW, GLFW already inited. Nothing happens");
            } else {
                logger.info("Initing GLFW");

                // inits GLFW and GL
                boolean glfwInited = glfwInit();

                if (!glfwInited) {
                    logger.error("Could not initialize GLFW");
                    throw new IllegalStateException("Could not initialize GLFW!");
                }

                GLFWErrorCallback.createPrint(System.err).set();  // TODO: should be connected to slf4j logger

                glfwDefaultWindowHints(); // optional, the current window hints are already the default
                glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
                glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE); // the window will not be resizable
                GLFW_intied = true;
            }
            GLFW_users++;
        }
    }

    private void destroyGLFW() {
        synchronized (GLFW_set_inited_lock) {
            if (--GLFW_users == 0) {
                glfwTerminate();
                GLFW_intied = false;
            }
        }
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

    public void setMouseScrollCallback(WindowEventCallback.OnScroll callback) {
        glfwSetScrollCallback(windowId, (long window, double xoffset, double yoffset) ->
                callback.invoke(this, (float) xoffset, (float) yoffset)
        );
    }

    public void terminate() {
        glfwDestroyWindow(windowId);
        destroyGLFW();
    }
}
