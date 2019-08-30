package sol_game;

import sol_engine.ecs.Component;

public class MoveCircularComp extends Component {

    public float centerX, centerY;
    public float radius;
    public float currAngle;

    public MoveCircularComp(float centerX, float centerY, float radius) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = radius;
    }

}
