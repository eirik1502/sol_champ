package sol_engine.core;

import sol_engine.ecs.Component;

public class SimpleKeyControlComp extends Component {

    public float velocity = 60;

    public SimpleKeyControlComp() {
    }

    public SimpleKeyControlComp(float velocity) {
        this.velocity = velocity;
    }
}
