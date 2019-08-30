package sol_engine.physics_module;

import org.joml.Vector2f;
import sol_engine.ecs.Component;

public class PhysicsBodyComp extends Component {
//    public final static float DEFAULT_MASS = 50.0f;
//    public final static float DEFAULT_FRICTION_CONST = 0.5f;
//    public final static float DEFAULT_ELASTICITY = 0.7f;

    public static final float INF_MASS = 0;


    public Vector2f velocity = new Vector2f();
    public Vector2f acceleration = new Vector2f();
    public Vector2f impulse = new Vector2f();

    public float mass = 1f;
    public float frictionConst = 0.5f;
    public float elasticity = 1;


    public PhysicsBodyComp() {
    }

    public PhysicsBodyComp(float mass) {
        this.mass = mass;
    }

    public PhysicsBodyComp(float mass, float frictionConst, float elasticity) {
        this.mass = mass;
        this.frictionConst = frictionConst;
        this.elasticity = elasticity;
    }

    public PhysicsBodyComp(Vector2f velocity) {
        this.velocity.set(velocity);
    }

    @Override
    public Component clone() {
        PhysicsBodyComp comp = (PhysicsBodyComp) super.clone();
        comp.velocity = new Vector2f(velocity);
        comp.acceleration = new Vector2f(acceleration);
        comp.impulse = new Vector2f(impulse);
        return comp;
    }
}
