package sol_engine.engine_interface;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sol_engine.utils.tickers.Ticker;

// Don't use this class yet
// TODO: clean this up, should extends SimulationLoop?

public class ThreadedSimulationLoop {
    private final Logger logger = LoggerFactory.getLogger(ThreadedSimulationLoop.class);

    private Thread thread;
    private SimulationLoop simulationLoop;


    public ThreadedSimulationLoop(SimulationLoop simulationLoop) {
        this.simulationLoop = simulationLoop;
    }

    public ThreadedSimulationLoop(SolSimulation simulation) {
        simulationLoop = new SimulationLoop(simulation);
    }

    public ThreadedSimulationLoop(SolSimulation simulation, float tickFrequency) {
        simulationLoop = new SimulationLoop(simulation, tickFrequency);
    }

    public ThreadedSimulationLoop(SolSimulation simulation, Ticker stepTicker) {
        simulationLoop = new SimulationLoop(simulation, stepTicker);
    }

    public void start() {
        thread = new Thread(() -> {
            simulationLoop.start();
        }, "SimulationLoop");
        thread.start();
    }

    public void startAndWaitUntilFinished() {
        start();
        waitUntilFinished();
    }

    public void waitUntilFinished() {
        if (thread != null) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                logger.warn("Interrupted while waiting for simulation to finish");
                e.printStackTrace();
            }
        } else {
            logger.warn("Trying to wait for simulation before it is started");
        }
    }

    public void terminate() {
        simulationLoop.terminate();  // should make the thread stop
        waitUntilFinished();
    }
}
