package sol_engine.core;

import org.joml.Vector2f;
import sol_engine.ecs.Component;

public class TransformComp extends Component {


    public final Vector2f position;
    public final Vector2f scale;
    public float rotationZ;


    public TransformComp() {
        this(0, 0);
    }

    public TransformComp(float x, float y) {
        position = new Vector2f(x, y);
        scale = new Vector2f(1, 1);
    }

    public TransformComp setX(float x) {
        this.position.x = x;
        return this;
    }

    public float getX() {
        return this.position.x;
    }

    public TransformComp setY(float y) {
        this.position.y = y;
        return this;
    }

    public float getY() {
        return this.position.y;
    }

    public TransformComp setPosition(float x, float y) {
        this.position.set(x, y);
        return this;
    }

    public TransformComp setPosition(Vector2f position) {
        this.position.set(position);
        return this;
    }

    public Vector2f getPosition() {
        return position;
    }

    public TransformComp clone() {
        TransformComp transComp = new TransformComp();
        transComp.position.set(position);
        transComp.scale.set(scale);
        transComp.rotationZ = rotationZ;
        return transComp;
    }
}
