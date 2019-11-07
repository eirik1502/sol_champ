package sol_engine.game_utils;

import sol_engine.ecs.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MoveByVelocityComp extends Component {

    public float velocity = 60;

    // in the order: left - right - up - down
    public List<String> directionalInput = new ArrayList<>();

    public MoveByVelocityComp() {
    }

    public MoveByVelocityComp(float velocity) {
        this.velocity = velocity;
    }

    public MoveByVelocityComp(float velocity, String... directionalInput) {
        this(velocity);
        this.directionalInput.addAll(Arrays.asList(directionalInput));
    }
}
