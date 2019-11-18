package sol_engine.game_utils;

import org.joml.Vector2f;
import sol_engine.ecs.SystemBase;
import sol_engine.physics_module.PhysicsBodyComp;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Changes an entities position in the direction of the arrow keys (or wasd) held.
 */
public class MoveByVelocitySystem extends SystemBase {

    private Vector2f moveDirection = new Vector2f();

    @Override
    public void onSetup() {
        usingComponents(PhysicsBodyComp.class, MoveByVelocityComp.class, UserInputComp.class);
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onUpdate() {
        entities.forEach(entity -> {
            MoveByVelocityComp moveVelComp = entity.getComponent(MoveByVelocityComp.class);
            PhysicsBodyComp physComp = entity.getComponent(PhysicsBodyComp.class);
            UserInputComp uInpComp = entity.getComponent(UserInputComp.class);

            if (moveVelComp.disabled) return;

            List<Float> directionalSpeed = moveVelComp.directionalInput.stream()
                    .map(inp -> uInpComp.checkPressed(inp) ? 1f : 0f)
                    .collect(Collectors.toList());

            if (directionalSpeed.size() < 4)
                return;

            moveDirection.x = directionalSpeed.get(1) - directionalSpeed.get(0);
            moveDirection.y = directionalSpeed.get(3) - directionalSpeed.get(2);

            if (moveDirection.lengthSquared() != 0) {
                moveDirection.normalize().mul(moveVelComp.velocity);
            }

            physComp.impulse.add(moveDirection);
        });
    }

    @Override
    public void onEnd() {

    }
}
