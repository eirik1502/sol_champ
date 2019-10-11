package sol_engine.core;

import org.joml.Vector2f;
import sol_engine.input_module.InputConsts;
import sol_engine.input_module.InputModule;
import sol_engine.physics_module.PhysicsBodyComp;

/**
 * Changes an entities position in the direction of the arrow keys (or wasd) held.
 */
public class SimpleKeyControlSystem extends ModuleSystemBase {

    private Vector2f moveDirection = new Vector2f();

    @Override
    public void onSetup() {
        usingModules(InputModule.class);
        usingComponents(PhysicsBodyComp.class, SimpleKeyControlComp.class);
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onUpdate() {
        entities.forEach(entity -> {
            SimpleKeyControlComp keyCtrlComp = entity.getComponent(SimpleKeyControlComp.class);
            PhysicsBodyComp physComp = entity.getComponent(PhysicsBodyComp.class);

            InputModule inp = super.getModule(InputModule.class);
            float left = inp.keyHeld(InputConsts.KEY_LEFT) || inp.keyHeld(InputConsts.KEY_A) ? 1 : 0;
            float right = inp.keyHeld(InputConsts.KEY_RIGHT) || inp.keyHeld(InputConsts.KEY_D) ? 1 : 0;
            float up = inp.keyHeld(InputConsts.KEY_UP) || inp.keyHeld(InputConsts.KEY_W) ? 1 : 0;
            float down = inp.keyHeld(InputConsts.KEY_DOWN) || inp.keyHeld(InputConsts.KEY_S) ? 1 : 0;

            moveDirection.x = right - left;
            moveDirection.y = down - up;

            if (moveDirection.lengthSquared() != 0) {
                moveDirection.normalize().mul(keyCtrlComp.velocity);
            }

            physComp.impulse.add(moveDirection);
        });
    }

    @Override
    public void onEnd() {

    }
}
