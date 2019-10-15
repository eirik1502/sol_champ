package sol_engine.engine_interface;

import sol_engine.utils.tickers.Ticker;

// Don't use this class yet
// TODO: clean this up, should extends SimulationLoop?

public class ThreadedSimulationLoop extends Thread {


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

    @Override
    public void run() {
        super.run();
        simulationLoop.start();
    }

    public void terminate() {
        simulationLoop.terminate();
    }
}
