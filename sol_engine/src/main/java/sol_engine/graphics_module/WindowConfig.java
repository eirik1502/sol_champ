package sol_engine.graphics_module;

public class WindowConfig {

    public float relWidth = 1;
    public float relHeight = 1;
    public String title = "SOL simulation";
    public boolean vsync = true;


    public WindowConfig(float relWidth, float relHeight, String title, boolean vsync) {
        this.relWidth = relWidth;
        this.relHeight = relHeight;
        this.title = title;
        this.vsync = vsync;
    }

    public WindowConfig(float relWidth, float relHeight, String title) {
        this(relWidth, relHeight, title, true);
    }
}
