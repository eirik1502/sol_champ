package sol_engine.game_utils;

import sol_engine.ecs.Component;

public class DestroySelfTimedComp extends Component {
    // user controlled
    public int timeFrames = 60;

    // system controlled
    public int counter = 0;

    public DestroySelfTimedComp() {
    }

    public DestroySelfTimedComp(int timeFrames) {
        this.timeFrames = timeFrames;
    }
}
