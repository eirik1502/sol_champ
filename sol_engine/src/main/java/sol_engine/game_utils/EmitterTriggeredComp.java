package sol_engine.game_utils;

import sol_engine.ecs.Component;

public class EmitterTriggeredComp extends Component {

    // user set

    //Must have a PhysicsBodyComp
    public String emitEntityClass;

    /**
     * Direction to emit entity, in the range [0, PI]
     * If not set, using transform rotation
     */
    public float emitDirection = -1;

    public float emitSpeed = 10;


    // system set
    public boolean trigger = false;

}
