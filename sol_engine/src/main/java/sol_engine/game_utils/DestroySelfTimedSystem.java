package sol_engine.game_utils;

import sol_engine.ecs.SystemBase;

public class DestroySelfTimedSystem extends SystemBase {
    @Override
    protected void onSetup() {
        usingComponents(DestroySelfTimedComp.class);
    }

    @Override
    protected void onUpdate() {
        forEachWithComponents(
                DestroySelfTimedComp.class,
                (entity, destroySelfTimedComp) -> {
                    if (destroySelfTimedComp.counter++ >= destroySelfTimedComp.timeFrames) {
                        world.removeEntity(entity);
                    }
                }
        );
    }
}
