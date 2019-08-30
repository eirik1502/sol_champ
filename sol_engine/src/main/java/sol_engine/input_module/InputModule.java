package sol_engine.input_module;

import org.joml.Vector2f;
import sol_engine.graphics_module.GraphicsModule;
import sol_engine.graphics_module.Window;
import sol_engine.module.Module;

public class InputModule extends Module {

    private Window window;

    private final boolean[] keysHeld;
    private final boolean[] mouseButtonsHeld;
    private final Vector2f cursorPosition;

    public InputModule() {
        keysHeld = new boolean[InputConsts.KEY_LAST];
        mouseButtonsHeld = new boolean[InputConsts.MOUSE_BUTTON_LAST];
        cursorPosition = new Vector2f();
    }

    public Vector2f cursorPosition() {
        return new Vector2f(this.cursorPosition);
    }

    public Vector2f cursorPosition(Vector2f into) {
        return into.set(this.cursorPosition);
    }

    public boolean keyHeld(int key) {
        return this.keysHeld[key];
    }

    public boolean mouseButtonHeld(int mouseButton) {
        return this.mouseButtonsHeld[mouseButton];
    }

    @Override
    public void onStart() {
        window = getModule(GraphicsModule.class).getWindow();

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
            this.cursorPosition.set(xpos, ypos);
        });
    }

    @Override
    public void onEnd() {
    }

    @Override
    public void onUpdate() {

    }
}
