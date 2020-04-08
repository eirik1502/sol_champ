package sol_engine.engine_interface;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sol_engine.utils.tickers.LinearTicker;
import sol_engine.utils.tickers.Ticker;

public class SimulationLoop {
    private final Logger logger = LoggerFactory.getLogger(SimulationLoop.class);

    public static final float DEFAULT_UNIFORM_TICK_FREQ = 1.0f / 60.0f;


    private SolSimulation simulation;
    private Ticker ticker;
    private boolean shouldTerminate = false;


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

    public void setup() {
        logger.info("setting up");
        simulation.setup();
    }

    public void start() {
        logger.info("starting");
        simulation.start();
        runBlocking();
    }

    private void runBlocking() {
        ticker.setListener(deltaTime -> {
            if (shouldTerminate) {
                logger.info("terminating");
                simulation.terminate();
            } else {
                simulation.step();
            }
            // simulation may be terminated by another condition, so this has to be checked as well
            if (simulation.isTerminated()) {
                ticker.stop();
            }
        });
        ticker.start();
    }

    public void terminate() {
        shouldTerminate = true;
    }

    public SolSimulation getSimulation() {
        return simulation;
    }
}
