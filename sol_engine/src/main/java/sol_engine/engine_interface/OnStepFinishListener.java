package sol_engine.engine_interface;

import sol_engine.ecs.World;

public interface OnStepFinishListener {
    void stepFinished(SolSimulation simulation);
}
