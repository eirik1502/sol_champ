package sol_engine.graphics_module;

import sol_engine.ecs.Component;
import sol_engine.graphics_module.graphical_objects.Renderable;
import sol_engine.graphics_module.graphical_objects.RenderableNull;

public class RenderShapeComp extends Component {

    public Renderable renderable;
    public float offsetX, offsetY;
    public float depth;


    public RenderShapeComp() {
        this(new RenderableNull());
    }

    public RenderShapeComp(Renderable renderable) {
        this(renderable, 0, 0);
    }

    public RenderShapeComp(Renderable renderable, float offsetX, float offsetY) {
        this(renderable, offsetX, offsetY, 0f);
    }

    public RenderShapeComp(Renderable renderable, float offsetX, float offsetY, float depth) {
        this.renderable = renderable;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.depth = depth;
    }

    @Override
    public void copy(Component other) {
        RenderShapeComp otherComp = (RenderShapeComp) other;
        renderable = otherComp.renderable == null ? null : otherComp.renderable.clone();
        offsetX = otherComp.offsetX;
        offsetY = otherComp.offsetY;
    }
}
