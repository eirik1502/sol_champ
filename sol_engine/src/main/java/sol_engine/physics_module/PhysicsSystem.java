package sol_engine.physics_module;

import org.joml.Vector2f;
import sol_engine.core.TransformComp;
import sol_engine.ecs.SystemBase;
import sol_engine.utils.math.MathF;

public class PhysicsSystem extends SystemBase {

    private final float maxSpeed = 30f * 60f;  // per second

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

            Vector2f accelerationPerFrame = physComp.acceleration.mul(PhysicsConstants.FIXED_UPDATE_TIME, new Vector2f());
            physComp.velocity.add(accelerationPerFrame);
            physComp.velocity.add(physComp.impulse);

            // apply friction
            float velocityDecreaseRatio = 1 - (physComp.frictionConst * PhysicsConstants.FIXED_UPDATE_TIME);
            physComp.velocity.mul(velocityDecreaseRatio);

            float speedSquared = physComp.velocity.lengthSquared();
            Vector2f cappedVelocity = (speedSquared > (maxSpeed * maxSpeed) && speedSquared != 0)
                    ? physComp.velocity.normalize(maxSpeed, new Vector2f())
                    : physComp.velocity;

            Vector2f velocityPerFrame = cappedVelocity.mul(PhysicsConstants.FIXED_UPDATE_TIME, new Vector2f());

            transComp.position.add(velocityPerFrame);

            physComp.acceleration.zero();
            physComp.impulse.zero();
        });
    }

    @Override
    public void onEnd() {

    }
}
