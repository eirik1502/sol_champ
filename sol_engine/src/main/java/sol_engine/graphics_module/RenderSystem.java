package sol_engine.graphics_module;

import sol_engine.core.ModuleSystemBase;
import sol_engine.core.TransformComp;

public class RenderSystem extends ModuleSystemBase {
    @Override
    public void onStart() {
        super.usingModules(GraphicsModule.class);
        super.usingComponents(RenderComp.class, TransformComp.class);

    }

    @Override
    public void onUpdate() {
        groupEntities.forEach(e -> {
            GraphicsModule graphics = getModule(GraphicsModule.class);

            RenderComp renderComp = e.getComponent(RenderComp.class);
            TransformComp transComp = e.getComponent(TransformComp.class);

            renderComp.renderable.setX( transComp.x );
            renderComp.renderable.setY( transComp.y );

            graphics.addRenderable(renderComp.renderable);
        });
    }

    @Override
    public void onEnd() {

    }
}
