package sol_engine.input_module;

import org.joml.Vector2f;
import sol_engine.graphics_module.GraphicsModule;
import sol_engine.graphics_module.Window;
import sol_engine.module.Module;

public class InputModule extends Module {

    private InputModuleConfig config;

    private Window window;

    private final Vector2f cursorPosScale = new Vector2f();

    private final boolean[] keysHeld = new boolean[InputConsts.KEY_LAST];
    private final boolean[] mouseButtonsHeld = new boolean[InputConsts.MOUSE_BUTTON_LAST];
    private final Vector2f cursorPosition = new Vector2f();


    public InputModule(InputModuleConfig config) {
        this.config = config;
    }

    public Vector2f cursorPosition() {
        return new Vector2f(this.cursorPosition);
    }

    public Vector2f cursorPosition(Vector2f into) {
        return into.set(this.cursorPosition);
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
        });

        window.setMouseButtonCallback((window1, button, action) -> {
            if (action == InputConsts.ACTION_PRESS) {
                this.mouseButtonsHeld[button] = true;
            } else if (action == InputConsts.ACTION_RELEASE) {
                this.mouseButtonsHeld[button] = false;
            }
        });

        window.setCursorPosCallback((window1, xpos, ypos) -> {
            this.cursorPosition.set(xpos, ypos).mul(cursorPosScale);
        });
    }

    @Override
    public void onEnd() {
    }

    @Override
    public void onUpdate() {

    }
}
