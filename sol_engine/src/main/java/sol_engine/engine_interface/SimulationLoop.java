package sol_engine.engine_interface;

import sol_engine.utils.tickers.Ticker;

public class SimulationLoop {

    public static final float DEFAULT_UNIFORM_TICK_FREQ = 1.0f/60.0f;


    private SolSimulation simulation;
    private boolean running;


    public SimulationLoop(SolSimulation simulation) {
        this(simulation, DEFAULT_UNIFORM_TICK_FREQ);
    }
    public SimulationLoop(SolSimulation simulation, float tickFrequency) {
        this(simulation, null);
    }
    public SimulationLoop(SolSimulation simulation, Ticker stepTicker) {
        this.simulation = simulation;
        this.running = false;
    }

    public void start() {
        simulation.start();
        this.running = true;
        runBlocking();
    }

    private void runBlocking() {
        while(running) {

            // time the simulation
            simulation.step();

            // check if the underlying simulation is ended, onEnd this loop
            if (simulation.isTerminated()) {
                terminate();
            }
        }

        if (!simulation.isTerminated()) {
            simulation.terminate();
        }
    }

    public void terminate() {
        running = false;
    }
}
