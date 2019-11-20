package sol_engine.graphics_module;

import org.joml.Vector3f;
import sol_engine.core.ModuleSystemBase;
import sol_engine.core.TransformComp;
import sol_engine.ecs.Entity;
import sol_engine.graphics_module.graphical_objects.Renderable;

import java.util.HashMap;
import java.util.Map;

public class RenderSystem extends ModuleSystemBase {


    private Map<Entity, Renderable> entityRenderables = new HashMap<>();


    @Override
    public void onSetup() {
        super.usingModules(GraphicsModule.class);
        super.usingComponents(RenderShapeComp.class, TransformComp.class);
    }

    @Override
    public void onStart() {
    }

    @Override
    protected void onUpdate() {
        GraphicsModule graphics = getModule(GraphicsModule.class);

        forEachWithComponents(
                RenderShapeComp.class,
                TransformComp.class,
                (entity, renderComp, transComp) -> {
                    Vector3f position = new Vector3f(
                            transComp.getX() + renderComp.offsetX,
                            transComp.getY() + renderComp.offsetY,
                            0
                    );
                    graphics.getRenderer().renderObject(renderComp.renderable, position, transComp.rotationZ);
                }
        );
    }

//    @Override
//    public void onUpdate() {
//        GraphicsModule graphics = getModule(GraphicsModule.class);
//
//        Set<Entity> entitySet = new HashSet<>(entities.asList());
//        Set<Entity> entityWithRenderablesSet = entityRenderables.keySet();
//
//        Set<Entity> entitiesToBeRemoved = Sets.difference(entityWithRenderablesSet, entitySet).immutableCopy();
////        Set<Entity> entitiesToBeAdded = Sets.difference(entitySet, entityWithRenderablesSet);
//
//        entitiesToBeRemoved.forEach(entity -> {
//            Renderable removedRenderable = entityRenderables.remove(entity);
//            graphics.removeRenderable(removedRenderable);
//        });
//
//        entities.forEach(e -> {
//            RenderShapeComp renderSquareComp = e.getComponent(RenderShapeComp.class);
//            TransformComp transComp = e.getComponent(TransformComp.class);
//
//            Renderable renderable = entityRenderables.computeIfAbsent(e, newE -> new Rectangle());
//            renderable.setProps(
//                    transComp.x + renderSquareComp.offsetX,
//                    transComp.y + renderSquareComp.offsetY,
//                    renderSquareComp.width, renderSquareComp.height,
//                    renderSquareComp.material
//            );
//
//            graphics.addRenderable(renderable);
//        });
//
//    }

    @Override
    public void onEnd() {

    }
}
