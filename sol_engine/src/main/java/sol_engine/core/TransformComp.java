package sol_engine.core;

import org.joml.Vector2f;
import sol_engine.ecs.Component;

public class TransformComp extends Component {

    public final Vector2f position = new Vector2f(0, 0);
    public final Vector2f scale = new Vector2f(1, 1);
    public float rotationZ;


    public TransformComp() {
    }

    public TransformComp(float x, float y) {
        position.set(x, y);
    }

    public TransformComp(Vector2f position) {
        this.position.set(position);
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

    public float getRotationZ() {
        return rotationZ;
    }

    public TransformComp setRotationZ(float value) {
        this.rotationZ = value;
        return this;
    }

//    public Component clone() {
//        TransformComp newComp = new TransformComp();
//        newComp.copy(this);
//        return newComp;
//    }

    public void copy(Component other) {
        TransformComp otherComp = (TransformComp) other;
        position.set(otherComp.position);
        scale.set(otherComp.scale);
        rotationZ = otherComp.rotationZ;
    }
}
