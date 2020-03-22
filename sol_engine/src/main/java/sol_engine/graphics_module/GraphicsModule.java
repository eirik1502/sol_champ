package sol_engine.graphics_module;

import sol_engine.graphics_module.render.Renderer;
import sol_engine.module.Module;

public class GraphicsModule extends Module {
    private GraphicsModuleConfig config;

    private Window window;
    private Renderer renderer;

    public GraphicsModule(GraphicsModuleConfig config) {
        this.config = config;
    }

    public Window getWindow() {
        return window;
    }

    public Renderer getRenderer() {
        return renderer;
    }

    @Override
    public void onSetup() {
        window = new Window(config.windowConfig);
        renderer = new Renderer(config.renderConfig, window.getRenderingContext());
    }

    @Override
    public void onStart() {
        window.show();
        window.focus();
    }

    @Override
    public void onEnd() {
        renderer.terminate();
        window.terminate();
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
