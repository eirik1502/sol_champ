package sol_engine.graphics_module;


public class GraphicsModuleConfig {

    public WindowConfig windowConfig;
    public RenderConfig renderConfig;

    public GraphicsModuleConfig(WindowConfig windowConfig, RenderConfig renderConfig) {
        this.windowConfig = windowConfig;
        this.renderConfig = renderConfig;
    }

}
