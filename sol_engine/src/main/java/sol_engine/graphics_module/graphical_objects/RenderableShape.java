package sol_engine.graphics_module.graphical_objects;

import sol_engine.graphics_module.materials.Material;
import sol_engine.graphics_module.materials.MattMaterial;
import sol_engine.graphics_module.render.Renderer;

public class RenderableShape {
    public static class Rectangle extends Renderable {

        public Rectangle(float width, float height, Material material) {
            super(width, height, Renderer.UNIT_CORNERED_RECTANGLE_MESH, material);
        }

        public Rectangle() {
            this(32, 32, MattMaterial.STANDARD());
        }
    }

    public static class Circle extends Renderable {

        public Circle(float radius, Material material) {
            super(radius * 2, radius * 2, Renderer.UNIT_CENTERED_CIRCLE_MESH, material);
        }

        public Circle() {
            this(16, MattMaterial.STANDARD());
        }
    }
}
