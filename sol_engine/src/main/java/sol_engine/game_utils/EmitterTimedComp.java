package sol_engine.game_utils;

import sol_engine.ecs.Component;

public class EmitterTimedComp extends Component {
    // user controlled

    public int timeFrames = 60;
    // -1 denotes infinity
    public int maxEmits = -1;

    //Must have a PhysicsBodyComp
    public String emitEntityClass;
    public String emitEntityName = "";

    /**
     * Direction to emit entity, in the range [0, PI]
     * If not set, using transform rotation
     */
    public float emitDirection = -1;

    public float emitSpeed = 10;


    // system controlled
    public int counter = 0;
    public int emitCount = 0;

    public EmitterTimedComp() {

    }

    public EmitterTimedComp(int timeFrames, int maxEmits, String emitEntityClass, String emitEntityName, float emitDirection, float emitSpeed) {
        this.timeFrames = timeFrames;
        this.maxEmits = maxEmits;
        this.emitEntityClass = emitEntityClass;
        this.emitEntityName = emitEntityName;
        this.emitDirection = emitDirection;
        this.emitSpeed = emitSpeed;
        this.counter = counter;
        this.emitCount = emitCount;
    }


}
