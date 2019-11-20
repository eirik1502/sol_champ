package sol_engine.game_utils;

import org.joml.Vector2f;
import sol_engine.core.TransformComp;
import sol_engine.ecs.SystemBase;
import sol_engine.physics_module.PhysicsBodyComp;
import sol_engine.utils.math.MathF;

public class EmitterTimedSystem extends SystemBase {
    @Override
    protected void onSetup() {
        usingComponents(EmitterTimedComp.class, TransformComp.class);
    }

    @Override
    protected void onUpdate() {
        forEachWithComponents(
                EmitterTimedComp.class,
                TransformComp.class,
                (entity, emitterTimedComp, transComp) -> {

                    if (emitterTimedComp.maxEmits != -1
                            && emitterTimedComp.emitCount >= emitterTimedComp.maxEmits) {
                        return;
                    }
                    if (emitterTimedComp.counter++ >= emitterTimedComp.timeFrames) {
                        String entityName = emitterTimedComp.emitEntityName.isEmpty()
                                ? "emitted by " + entity.name
                                : emitterTimedComp.emitEntityName;
                        Vector2f impulse = new Vector2f();
                        float direction = emitterTimedComp.emitDirection == -1
                                ? MathF.random() * MathF.PI * 2//transComp.rotZ
                                : emitterTimedComp.emitDirection;
                        float speed = emitterTimedComp.emitSpeed;

                        world.instanciateEntityClass(emitterTimedComp.emitEntityClass, entityName)
                                .modifyComponent(TransformComp.class, comp -> comp.setPosition(transComp.position))
                                .modifyComponent(PhysicsBodyComp.class, comp -> comp.impulse.add(
                                        MathF.lengthdirX(speed, direction),
                                        MathF.lengthdirY(speed, direction)
                                ));

                        emitterTimedComp.counter = 0;
                        emitterTimedComp.emitCount++;
                    }
                }
        );
    }
}
