package sol_engine.game_utils;

import sol_engine.core.TransformComp;
import sol_engine.ecs.Entity;
import sol_engine.ecs.World;

import java.util.ArrayList;
import java.util.List;

public class CollisionInteraction {

    public static interface Script {
        void execute(World world, Entity self, Entity other);
    }

    private List<Script> scripts = new ArrayList<>();

    public CollisionInteraction() {
    }

    void execute(World world, Entity self, Entity other) {
        this.scripts.forEach(script -> script.execute(world, self, other));
    }

    private CollisionInteraction addScript(Script script) {
        this.scripts.add(script);
        return this;
    }

    public CollisionInteraction none() {
        return addScript((world, self, other) -> {
        });
    }

    public CollisionInteraction custom(Script script) {
        return addScript(script);
    }

    public CollisionInteraction destroySelf() {
        return addScript((world, self, other) -> world.removeEntity(self));
    }

    public CollisionInteraction destroyOther() {
        return addScript((world, self, other) -> world.removeEntity(other));
    }

    public CollisionInteraction create(String className, String instanceName) {
        return addScript((world, self, other) -> {
            TransformComp transComp = self.getComponent(TransformComp.class);
            world.addEntity(instanceName, className)
                    .modifyComponent(TransformComp.class, comp -> comp.setPosition(transComp.position));
        });
    }

    public static CollisionInteraction None() {
        return new CollisionInteraction().none();
    }

    public static CollisionInteraction Custom(Script script) {
        return new CollisionInteraction().custom(script);
    }

    public static CollisionInteraction DestroySelf() {
        return new CollisionInteraction().destroySelf();
    }

    public static CollisionInteraction DestroyOther() {
        return new CollisionInteraction().destroyOther();
    }

    public static CollisionInteraction Create(String className, String instanceName) {
        return new CollisionInteraction().create(className, instanceName);
    }

}
