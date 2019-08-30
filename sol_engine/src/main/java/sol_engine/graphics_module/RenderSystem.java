package sol_engine.graphics_module;

import sol_engine.core.ModuleSystemBase;
import sol_engine.core.TransformComp;
import sol_engine.ecs.Entity;
import sol_engine.graphics_module.graphical_objects.Renderable;
import sol_engine.graphics_module.graphical_objects.Square;

import java.util.HashMap;
import java.util.Map;

public class RenderSystem extends ModuleSystemBase {


    private Map<Entity, Renderable> entityRenderables = new HashMap<>();


    @Override
    public void onStart() {
        super.usingModules(GraphicsModule.class);
        super.usingComponents(RenderSquareComp.class, TransformComp.class);

    }

    @Override
    public void onUpdate() {
        groupEntities.forEach(e -> {
            GraphicsModule graphics = getModule(GraphicsModule.class);

            RenderSquareComp renderSquareComp = e.getComponent(RenderSquareComp.class);
            TransformComp transComp = e.getComponent(TransformComp.class);

            Renderable renderable = entityRenderables.computeIfAbsent(e, newE -> new Square());
            ((Square) renderable).setProps(
                    transComp.x + renderSquareComp.offsetX,
                    transComp.y + renderSquareComp.offsetY,
                    renderSquareComp.width, renderSquareComp.height,
                    renderSquareComp.material
            );

            graphics.addRenderable(renderable);

            //TODO: remove renderables if an entity is no longer present
        });
    }

    @Override
    public void onEnd() {

    }
}
