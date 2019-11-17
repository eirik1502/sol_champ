package sol_examples.simple_shooter;

import org.joml.Vector2f;
import sol_engine.core.ModuleSystemBase;
import sol_engine.core.TransformComp;
import sol_engine.input_module.InputModule;
import sol_engine.physics_module.PhysicsBodyComp;

public class ShootSystem extends ModuleSystemBase {
    private Vector2f position = new Vector2f();
    private Vector2f initialDirectionVec = new Vector2f();
    private Vector2f initialBulletSpeed = new Vector2f();
    private Vector2f initialBulletPosition = new Vector2f();
    private Vector2f tempVec = new Vector2f();

    @Override
    public void onSetup() {
        usingModules(InputModule.class);
        usingComponents(ShootComp.class, TransformComp.class);
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onUpdate() {
        InputModule inp = super.getModule(InputModule.class);

        entities.forEach(entity -> {
            ShootComp shootComp = entity.getComponent(ShootComp.class);
            TransformComp transComp = entity.getComponent(TransformComp.class);

            if (shootComp.framesSinceLastShot >= shootComp.reloadFrames) {
                boolean shootRequested = inp.mouseButtonHeld(shootComp.shootMouseButton);
                if (shootRequested) {
                    position.set(transComp.x, transComp.y);
                    inp.cursorPosition().sub(position, initialDirectionVec).normalize();
                    initialDirectionVec.mul(shootComp.initialBulletSpeed, initialBulletSpeed);
                    position.add(initialDirectionVec.mul(40, tempVec), initialBulletPosition);

                    world.instanciateEntityClass(shootComp.bulletEntityClass, "bullet")
                            .modifyComponent(TransformComp.class, comp ->
                                    comp.setPosition(initialBulletPosition.x, initialBulletPosition.y))
                            .modifyComponent(PhysicsBodyComp.class, comp -> comp.impulse.add(initialBulletSpeed));

                    shootComp.framesSinceLastShot = 0;
                }
            } else {
                shootComp.framesSinceLastShot++;
            }
        });
    }

    @Override
    public void onEnd() {

    }
}
