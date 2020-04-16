package sol_engine.graphics_module;

public class RenderConfig {

    public float cameraX, cameraY;
    public float viewWidth, viewHeight;
    public boolean disableGui = false;

    public RenderConfig(float cameraX, float cameraY, float viewWidth, float viewHeight) {
        this(cameraX, cameraY, viewWidth, viewHeight, false);
    }

    public RenderConfig(float cameraX, float cameraY, float viewWidth, float viewHeight, boolean disableGui) {
        this.cameraX = cameraX;
        this.cameraY = cameraY;
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;
        this.disableGui = disableGui;
    }
}
