package sol_engine.graphics_module;

import sol_engine.ecs.Component;
import sol_engine.graphics_module.graphical_objects.Renderable;

public class RenderComp extends Component {

    public Renderable renderable;



    public RenderComp() {

    }
    public RenderComp(Renderable renderable) {
        this.renderable = renderable;
    }

}
