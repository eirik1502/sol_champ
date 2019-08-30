package sol_engine.graphics_module;

public class RenderConfig {

    public float x, y;
    public float width, height;

    public RenderConfig(float cameraX, float cameraY, float viewWidth, float viewHeigh) {
        this.x = cameraX;
        this.y = cameraY;
        this.width = viewWidth;
        this.height = viewHeigh;
    }
}
