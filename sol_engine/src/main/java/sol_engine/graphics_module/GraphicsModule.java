package sol_engine.graphics_module;

import sol_engine.graphics_module.render.Renderer;
import sol_engine.module.Module;

public class GraphicsModule extends Module {
    private Window window;
    private Renderer renderer;

    public GraphicsModule(GraphicsModuleConfig config) {

        window = new Window(config.windowConfig);
        renderer = new Renderer(config.renderConfig, window.getRenderingContext());
    }

    public Window getWindow() {
        return window;
    }

    public Renderer getRenderer() {
        return renderer;
    }

    @Override
    public void onSetup() {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onEnd() {
    }

    @Override
    public void onUpdate() {
        window.pollEvents();
        if (window.shouldClose()) {
            simulationShouldTerminate();
        }
        renderer.render();
    }
}
