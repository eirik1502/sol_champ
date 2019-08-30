import sol_engine.core.TransformComp;
import sol_engine.ecs.SystemBase;

public class MoveCircularSystem extends SystemBase {


    @Override
    public void onStart() {
        usingComponents(MoveCircularComp.class, TransformComp.class);
    }

    @Override
    public void onUpdate() {
        groupEntities.forEach(e -> {
            TransformComp transComp = e.getComponent(TransformComp.class);
            MoveCircularComp mvCircComp = e.getComponent(MoveCircularComp.class);
            mvCircComp.currAngle += 0.1;
            transComp.x = mvCircComp.centerX + (float)Math.cos(mvCircComp.currAngle) * mvCircComp.radius;
            transComp.y = mvCircComp.centerY + (float)Math.sin(mvCircComp.currAngle) * mvCircComp.radius;
        });
    }

    @Override
    public void onEnd() {

    }
}
