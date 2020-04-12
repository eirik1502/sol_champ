package sol_engine.physics_module;

import sol_engine.ecs.Component;
import sol_engine.ecs.Entity;

import java.util.HashMap;
import java.util.Map;

public class CollisionComp extends Component {

    public PhysicsBodyShape bodyShape;

    public Map<Entity, CollisionData> collidingEntities = new HashMap<>();


    public CollisionComp() {
        this.bodyShape = new PhysicsBodyShape.Circ(1);
    }

    public CollisionComp(PhysicsBodyShape bodyShape) {
        this.bodyShape = bodyShape;
    }


    @Override
    public void copy(Component other) {
        CollisionComp otherComp = (CollisionComp) other;
        bodyShape = otherComp.bodyShape.clone();
        collidingEntities = new HashMap<>(otherComp.collidingEntities);
    }
}