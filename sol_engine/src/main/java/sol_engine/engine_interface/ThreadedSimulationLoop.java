package sol_engine.engine_interface;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sol_engine.utils.Function;
import sol_engine.utils.mutable_primitives.MObject;
import sol_engine.utils.tickers.Ticker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

// Don't use this class yet
// TODO: clean this up, should extends SimulationLoop?

public class ThreadedSimulationLoop {
    public static interface TerminationCallback {
        public void onTermination(ThreadedSimulationLoop threadedSimLoop, SimulationLoop simLoop, SolSimulation sim);
    }

    private final Logger logger = LoggerFactory.getLogger(ThreadedSimulationLoop.class);

    private Thread thread;
    private SimulationLoop simulationLoop;

    private final Object waitStartLock = new Object();
    private AtomicBoolean setupComplete = new AtomicBoolean(false);
    private AtomicBoolean waitForNextStepFinished = new AtomicBoolean(false);

    private List<TerminationCallback> terminationCallbacks = new ArrayList<>();


    public ThreadedSimulationLoop(SimulationLoop simulationLoop) {
        this.simulationLoop = simulationLoop;

        thread = new Thread(() -> {
            logger.info("Setup on thread");
            simulationLoop.setup();
            logger.info("Setup on thread complete");
            setupComplete.set(true);
            synchronized (waitStartLock) {
                try {
                    waitStartLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            logger.info("Starting");
            simulationLoop.start();

            terminationCallbacks.forEach(callback ->
                    callback.onTermination(this, simulationLoop, simulationLoop.getSimulation()));
        }, "SimulationLoop_" + simulationLoop.getSimulation().getClass().getSimpleName());
    }

    public ThreadedSimulationLoop(SolSimulation simulation) {
        this(new SimulationLoop(simulation));
    }

    public ThreadedSimulationLoop(SolSimulation simulation, float tickFrequency) {
        this(new SimulationLoop(simulation, tickFrequency));
    }

    public ThreadedSimulationLoop(SolSimulation simulation, Ticker stepTicker) {
        this(new SimulationLoop(simulation, stepTicker));
    }

    public void onTermination(TerminationCallback callback) {
        terminationCallbacks.add(callback);
    }

    public void setup() {
        setupComplete.set(false);
        thread.start();
        while (!setupComplete.get()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        logger.info("Setup outside thread complete");
    }

    public void start() {
        if (!setupComplete.get()) {
            logger.error("Setup must be called through this threaded loop before starting");
        }
        synchronized (waitStartLock) {
            waitStartLock.notify();  // notify the thread so it startst the simulation
        }
    }

    public void onNextStepFinished(OnStepFinishListener callback) {
        simulationLoop.onNextStepFinished(callback);
    }

    public <T> T waitForNextStepFinish(Function.OneArgReturn<SolSimulation, T> producer) {
        MObject<T> retVal = new MObject<>();
        waitForNextStepFinished.set(true);
        simulationLoop.onNextStepFinished(simulation -> {
            retVal.value = producer.invoke(simulation);
            waitForNextStepFinished.set(false);
        });
        while (waitForNextStepFinished.get()) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return retVal.value;
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
