package sol_engine.physics_module;

import org.joml.Vector2f;
import sol_engine.core.TransformComp;
import sol_engine.ecs.SystemBase;
import sol_engine.utils.math.MathF;

public class PhysicsSystem extends SystemBase {

    private final Vector2f tempVec = new Vector2f();
    private final Vector2f newVelocity = new Vector2f();

    private final float maxSpeed = 62;

    @Override
    public void onSetup() {
        usingComponents(PhysicsBodyComp.class);
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onUpdate() {
        entities.forEach(entity -> {
            PhysicsBodyComp physComp = entity.getComponent(PhysicsBodyComp.class);
            TransformComp transComp = entity.getComponent(TransformComp.class);

            physComp.velocity.add(physComp.acceleration.mul(PhysicsConstants.FIXED_UPDATE_TIME, tempVec));
            physComp.velocity.add(physComp.impulse);

            // apply friction
            physComp.velocity.mul(1 - physComp.frictionConst * PhysicsConstants.FIXED_UPDATE_TIME);

            physComp.velocity.mul(PhysicsConstants.FIXED_UPDATE_TIME, newVelocity);

            float newSpeedSquared = newVelocity.lengthSquared();
            if (newSpeedSquared > (maxSpeed * maxSpeed) && newSpeedSquared != 0) {
                newVelocity.normalize(maxSpeed);
            }

            transComp.position.add(newVelocity);

            physComp.acceleration.zero();
            physComp.impulse.zero();
        });
    }

    @Override
    public void onEnd() {

    }
}
