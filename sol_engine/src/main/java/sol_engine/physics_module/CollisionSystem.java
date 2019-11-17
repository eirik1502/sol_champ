package sol_engine.physics_module;

import sol_engine.core.TransformComp;
import sol_engine.ecs.SystemBase;

public class CollisionSystem extends SystemBase {
    @Override
    public void onSetup() {
        usingComponents(CollisionComp.class, TransformComp.class);
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onUpdate() {
        long startTime = System.nanoTime();
        // Doing a double travers of all entities to determine collisions, not optimal.
        entities.forEach(entity -> {
            CollisionComp collComp = entity.getComponent(CollisionComp.class);
            TransformComp transComp = entity.getComponent(TransformComp.class);

            collComp.collidingEntities.clear();

            entities.stream()
                    .filter(otherEntity -> otherEntity != entity)
                    .forEach(otherEntity -> {
                        CollisionComp otherCollComp = otherEntity.getComponent(CollisionComp.class);
                        TransformComp otherTransComp = otherEntity.getComponent(TransformComp.class);

                        CollisionData collData = new CollisionData();

                        boolean isCollision = PhysicsBodyCollisionFunctions.collision(
                                transComp.position, collComp.bodyShape,
                                otherTransComp.position, otherCollComp.bodyShape,
                                collData);

                        if (isCollision) {
                            collComp.collidingEntities.put(otherEntity, collData);
                        }
                    });
        });
        long totalTime = System.nanoTime() - startTime;
//        System.out.println("Collisions calculated in: " + totalTime * 0.000001);
    }

    @Override
    public void onEnd() {

    }
}
