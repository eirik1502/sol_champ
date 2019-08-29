package sol_engine.graphics_module;

import sol_engine.ecs.Component;
import sol_engine.graphics_module.materials.Material;

public class RenderSquareComp extends Component {

    public float width, height;
    public float offsetX, offsetY;
    public Material material;


    public RenderSquareComp() {

    }

    public RenderSquareComp(float width, float height, Material material) {
        this.width = width;
        this.height = height;
        this.material = material;
    }

    public RenderSquareComp(float width, float height, Material material, float offsetX, float offsetY) {
        this(width, height, material);
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

}
