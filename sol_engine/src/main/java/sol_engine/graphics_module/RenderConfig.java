package sol_engine.graphics_module;

public class RenderConfig {

    public float cameraX, cameraY;
    public float viewWidth, viewHeight;

    public RenderConfig(float cameraX, float cameraY, float viewWidth, float viewHeight) {
        this.cameraX = cameraX;
        this.cameraY = cameraY;
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;
    }
}
