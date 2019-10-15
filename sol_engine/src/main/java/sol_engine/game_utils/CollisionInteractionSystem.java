package sol_engine.game_utils;

import com.google.common.collect.Sets;
import sol_engine.ecs.EntitiesUtils;
import sol_engine.ecs.Entity;
import sol_engine.ecs.SystemBase;
import sol_engine.physics_module.CollisionComp;

import java.util.HashSet;
import java.util.Set;

public class CollisionInteractionSystem extends SystemBase {
    @Override
    public void onSetup() {
        usingComponents(CollisionComp.class, CollisionInteractionComp.class);
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onUpdate() {
        Set<Entity> entitiesSet = new HashSet<>(entities.copyToList());

        forEachWithComponents(
                CollisionComp.class,
                CollisionInteractionComp.class,

                (entity, collComp, collIntComp) -> {
                    Set<Entity> collidingEntities = collComp.collidingEntities.keySet();

                    Set<String> interactingTags = collIntComp.interactionByTag.keySet();

                    Set<Entity> collidingEntitiesWithInteractionComp = Sets.intersection(collidingEntities, entitiesSet);

                    EntitiesUtils.ForEachWithComponents(
                            collidingEntitiesWithInteractionComp.stream(),
                            CollisionInteractionComp.class,

                            (otherEntity, otherCollIntComp) -> {
                                Set<String> otherTags = otherCollIntComp.tags;
                                Sets.intersection(interactingTags, otherTags).forEach(tag -> {
                                    collIntComp.interactionByTag.get(tag).execute(world, entity, otherEntity);
                                });
                            }
                    );

                }
        );
    }

    @Override
    public void onEnd() {

    }
}
