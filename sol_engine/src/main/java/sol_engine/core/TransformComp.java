package sol_engine.core;

import sol_engine.ecs.Component;

public class TransformComp extends Component {


    public float x, y;
    public float scaleX, scaleY;
    public float rotZ;


    public TransformComp() {
    }

    public TransformComp(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public TransformComp setX(float x) {
        this.x = x;
        return this;
    }

    public TransformComp setY(float y) {
        this.y = y;
        return this;
    }

    public TransformComp setXY(float x, float y) {
        return setX(x).setY(y);
    }
}
