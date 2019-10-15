package sol_engine.engine_interface;

import sol_engine.utils.tickers.LinearTicker;
import sol_engine.utils.tickers.Ticker;

public class SimulationLoop {

    public static final float DEFAULT_UNIFORM_TICK_FREQ = 1.0f / 60.0f;


    private SolSimulation simulation;
    private Ticker ticker;


    public SimulationLoop(SolSimulation simulation) {
        this(simulation, DEFAULT_UNIFORM_TICK_FREQ);
    }

    public SimulationLoop(SolSimulation simulation, float tickFrequency) {
        this(simulation, new LinearTicker(tickFrequency));
    }

    public SimulationLoop(SolSimulation simulation, Ticker stepTicker) {
        this.simulation = simulation;
        this.ticker = stepTicker;
    }

    public void start() {
        simulation.start();
        runBlocking();
    }

    private void runBlocking() {
        ticker.setListener(deltaTime -> {
            simulation.step();
            if (simulation.isTerminated()) {
                terminate();
            }
        });
        ticker.start();
    }

    public void terminate() {
        ticker.stop();
        simulation.terminate();
    }
}
