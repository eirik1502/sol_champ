package sol_examples.simple_sol_game;

import sol_engine.ecs.World;
import sol_engine.engine_interface.SimulationLoop;
import sol_engine.engine_interface.SolSimulation;
import sol_engine.engine_interface.ThreadedSimulationLoop;

public class SimpleSolGame extends SolSimulation {

    @Override
    protected void setup() {
        System.out.println("Simple SOL game started!");
        terminate();
    }


    public static void main(String[] args) {

        // 3 ways to use the simulation will now be presented

        // method 1: run the game simulation in a continous, blocking, loop
        runSimulationInLoop();

        // method 2: run the simulation in a threaded loop
        runSimulationInThreadedLoop();

        // method 3: manually run the simulation without a loop
        runSimulationManually();
    }

    /**
     * Run the game simulation as a normal game
     * This will run the game continously until terminated
     */
    private static void runSimulationInLoop() {
        // create a game instance
        SolSimulation game = new SimpleSolGame();

        // put the game simulation in a loop
        SimulationLoop gameLoop = new SimulationLoop(game);

        // setup the game loop,
        // it will run and block until the loop is terminated or the game is terminated
        gameLoop.start();

        // at the onEnd of the game, the state may be retrieved
        // this may be used for AI agents that want to learn from the simulation outcome
        World world = game.getGameState();
    }

    /**
     * Run the game simulation as a normal game, but threaded
     * This will run the game continously until terminated on its own thread
     */
    private static void runSimulationInThreadedLoop() {
        // create a game instance
        SolSimulation game = new SimpleSolGame();

        // put the game simulation in a loop
        ThreadedSimulationLoop threadedGameLoop = new ThreadedSimulationLoop(game);

        // setup the game loop,
        // it will run on its own thread until the loop is terminated or the game is terminated
        threadedGameLoop.start();

        // wait for the threaded game loop to terminate
        // this may be handeled in other ways
        try {
            threadedGameLoop.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // at the onEnd of the game, the state may be retrieved
        // this may be used for AI agents that want to learn from the simulation outcome
        World world = game.getGameState();
    }

    /**
     * Presents how to use the game as a simulation directly
     * This may be usefull for AI mathods to interact with the world
     */
    private static void runSimulationManually() {
        // create a game instance
        SolSimulation game = new SimpleSolGame();

        // setup the simulation, this will initialize everything needed for the simulation
        game.start();

        // manually step the simulation forward
        game.step();

        // you may retrieve the game state after a step
        World world = game.getGameState();

        // or look at the game state after 10 more simulation steps
        for (int i = 0; i < 10; i++) {
            game.step();
        }
        world = game.getGameState();
    }


}
