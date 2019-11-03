package sol_engine.game_utils;

import sol_engine.ecs.Component;

public class UserInputMoveVelocityComp extends Component {

    public float velocity = 60;

    public UserInputMoveVelocityComp() {
    }

    public UserInputMoveVelocityComp(float velocity) {
        this.velocity = velocity;
    }
}
