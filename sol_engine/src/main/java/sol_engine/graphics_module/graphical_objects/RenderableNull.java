package sol_engine.graphics_module.graphical_objects;

import sol_engine.graphics_module.materials.NullMaterial;
import sol_engine.graphics_module.render.Renderer;

public class RenderableNull extends Renderable {
    public RenderableNull() {
        super(0, 0, Renderer.NULL_MESH, new NullMaterial());
    }
}
