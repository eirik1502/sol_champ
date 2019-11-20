package sol_engine.physics_module;

import sol_engine.ecs.Component;
import sol_engine.ecs.Entity;

import java.util.HashMap;
import java.util.Map;

public class CollisionComp extends Component {

    public PhysicsBodyShape bodyShape;


    //    public Map<String, >
    public Map<Entity, CollisionData> collidingEntities = new HashMap<>();


    public CollisionComp() {
        this.bodyShape = new PhysicsBodyShape.Circ(1);
    }

    public CollisionComp(PhysicsBodyShape bodyShape) {
        this.bodyShape = bodyShape;
    }

    @Override
    public Component clone() {
        CollisionComp comp = (CollisionComp) super.clone();
        comp.bodyShape = bodyShape.clone();
        comp.collidingEntities = new HashMap<>();
        return comp;
    }
}
