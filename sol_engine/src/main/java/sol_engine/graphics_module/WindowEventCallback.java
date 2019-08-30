package sol_engine.graphics_module;

public interface WindowEventCallback {
    interface OnKey {
        void invoke(Window window, int key, int scancode, int action);
    }

    interface OnMouseButton {
        void invoke(Window window, int button, int action);
    }

    interface OnCursorPos {
        void invoke(Window window, float xpos, float ypos);
    }
}
