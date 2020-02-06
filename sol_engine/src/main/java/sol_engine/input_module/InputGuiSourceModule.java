package sol_engine.input_module;

import org.joml.Vector2f;
import sol_engine.graphics_module.GraphicsModule;
import sol_engine.graphics_module.Window;
import sol_engine.graphics_module.render.Renderer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InputGuiSourceModule extends InputSourceModule {

    private InputGuiSourceModuleConfig config;

    private Window window;
    private Renderer renderer;

    private final Vector2f cursorPosScale = new Vector2f();

    private Map<String, Integer> triggerActionsKeyMap = new HashMap<>();
    private Map<String, Integer> triggerActionsMouseButtonMap = new HashMap<>();
    private String cursorActionMap;
    private List<String> cursorCoordsActionsMap = Arrays.asList("", "");

    private final boolean[] keysHeld = new boolean[InputConsts.KEY_LAST];
    private final boolean[] mouseButtonsHeld = new boolean[InputConsts.MOUSE_BUTTON_LAST];
    private final Vector2f cursorPosition = new Vector2f();


    public InputGuiSourceModule(InputGuiSourceModuleConfig config) {
        this.config = config;
        config.triggerActionsMap.forEach((label, inputConst) -> {
            if (inputConst <= InputConsts.KEY_LAST) {
                triggerActionsKeyMap.put(label, inputConst);
            } else if (inputConst <= InputConsts.MOUSE_BUTTON_LAST) {
                triggerActionsMouseButtonMap.put(label, inputConst);
            } else if (inputConst == InputConsts.CURSOR_VEC) {
                cursorActionMap = label;
            } else if (inputConst == InputConsts.CURSOR_X) {
                cursorCoordsActionsMap.set(0, label);
            } else if (inputConst == InputConsts.CURSOR_Y) {
                cursorCoordsActionsMap.set(1, label);
            }
        });
    }

    public InputGuiSourceModule() {
    }


    @Override
    public boolean checkAction(String label) {
        if (triggerActionsKeyMap.containsKey(label)) {
            return checkKeyPressed(triggerActionsKeyMap.get(label));
        } else if (triggerActionsMouseButtonMap.containsKey(label)) {
            return checkMouseButtonPressed(triggerActionsMouseButtonMap.get(label));
        }
        return false;
    }

    @Override
    public float floatInput(String label) {
        int coordIndex = cursorCoordsActionsMap.indexOf(label);
        if (coordIndex != -1) {
            return cursorPosition.get(coordIndex);
        }
        return 0;
    }

    @Override
    public Vector2f vectorInput(String label) {
        if (label.equals(cursorActionMap)) {
            return new Vector2f(cursorPosition);
        }
        return new Vector2f();
    }

    public boolean checkKeyPressed(int key) {
        return keysHeld[key];
    }

    public boolean checkMouseButtonPressed(int button) {
        return mouseButtonsHeld[InputConsts.inputConstToGlfw(button)];
    }

    public Vector2f cursorPosition() {
        return cursorPosition;
    }

    public boolean keyHeld(int key) {
        if (!checkRangeIncluded(0, this.keysHeld.length - 1, key)) {
            return false;
        }
        return this.keysHeld[key];
    }

    public boolean mouseButtonHeld(int mouseButton) {
        if (!checkRangeIncluded(0, this.mouseButtonsHeld.length - 1, mouseButton)) {
            return false;
        }
        return this.mouseButtonsHeld[mouseButton];
    }

    private boolean checkRangeIncluded(int lower, int upper, int value) {
        return value >= lower && value <= upper;
    }

    @Override
    public void onSetup() {
        usingModules(GraphicsModule.class);
    }

    @Override
    public void onStart() {
        window = getModule(GraphicsModule.class).getWindow();
        renderer = getModule(GraphicsModule.class).getRenderer();

        if (config.cursorPosScaleToSize == null || config.cursorPosScaleToSize.equals(new Vector2f())) {
            cursorPosScale.set(1, 1);
        } else {
            Vector2f windowSize = window.getWindowSize();
            cursorPosScale.set(
                    config.cursorPosScaleToSize.x / windowSize.x,
                    config.cursorPosScaleToSize.y / windowSize.y
            );
        }

        window.setKeyCallback((window1, key, scancode, action) -> {
            if (action == InputConsts.ACTION_PRESS) {
                this.keysHeld[key] = true;
            } else if (action == InputConsts.ACTION_RELEASE) {
                this.keysHeld[key] = false;
            }

            if (renderer.getGui().io.getWantCaptureKeyboard()) {
                this.keysHeld[key] = false;
            }
        });

        window.setMouseButtonCallback((window1, button, action) -> {
            if (action == InputConsts.ACTION_PRESS) {
                this.mouseButtonsHeld[button] = true;
            } else if (action == InputConsts.ACTION_RELEASE) {
                this.mouseButtonsHeld[button] = false;
            }

            if (renderer.getGui().io.getWantCaptureMouse()) {
                this.mouseButtonsHeld[button] = false;
            }
        });

        window.setCursorPosCallback((window1, xpos, ypos) -> {
            if (!renderer.getGui().io.getWantCaptureMouse()) {
                this.cursorPosition.set(xpos, ypos).mul(cursorPosScale);
            }
        });

        window.setMouseScrollCallback((window1, offsetX, offsetY) -> {
            renderer.getGui().scroll = offsetY;
        });
    }

    @Override
    public void onEnd() {
    }

    @Override
    public void onUpdate() {

    }
}
