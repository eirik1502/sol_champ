package sol_game.sol_client;

import sol_engine.engine_interface.SimulationLoop;
import sol_engine.engine_interface.SolSimulation;

public class MainClient {

    public static void main(String[] args) {


        SolSimulation solClient = new ClientSimulation();

        SimulationLoop solClientLoop = new SimulationLoop(solClient);

        solClientLoop.start();

    }

}
