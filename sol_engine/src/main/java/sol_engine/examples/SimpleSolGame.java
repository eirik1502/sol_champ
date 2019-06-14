package sol_engine.examples;

import sol_engine.engine_interface.SimulationLoop;
import sol_engine.engine_interface.SolSimulation;
import sol_engine.engine_interface.ThreadedSimulationLoop;

public class SimpleSolGame {

    public static void main(String[] args) {

        SolSimulation game = new SolSimulation() {
            @Override
            protected void onStart() {
                System.out.println("Simple SOL game started!");
                terminate();
            }
        };

        SimulationLoop gameLoop = new SimulationLoop(game);
        gameLoop.start();

        ThreadedSimulationLoop threadedGameLoop = new ThreadedSimulationLoop(game);
        threadedGameLoop.start();
        try {
            threadedGameLoop.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
