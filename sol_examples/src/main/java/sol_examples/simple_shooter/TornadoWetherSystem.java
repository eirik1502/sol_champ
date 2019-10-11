package sol_examples.simple_shooter;

import org.joml.Vector2f;
import sol_engine.core.TransformComp;
import sol_engine.ecs.SystemBase;
import sol_engine.physics_module.PhysicsBodyComp;

public class TornadoWetherSystem extends SystemBase {

    private Vector2f worldCenter = new Vector2f(1600f / 2f, 900f / 2f);

    @Override
    protected void onSetup() {
        usingComponents(PhysicsBodyComp.class, TransformComp.class);
    }

    @Override
    public void onUpdate() {
        entities.forEach(entity -> {
            PhysicsBodyComp physComp = entity.getComponent(PhysicsBodyComp.class);
            TransformComp transComp = entity.getComponent(TransformComp.class);
            if (physComp.mass != PhysicsBodyComp.INF_MASS) {
                Vector2f position = new Vector2f(transComp.x, transComp.y);
                Vector2f windAcceleration = new Vector2f();
                worldCenter.sub(position, windAcceleration)
                        .normalize()
                        .perpendicular()
                        .mul(60);
                physComp.acceleration.add(windAcceleration);
            }
        });
    }
}
