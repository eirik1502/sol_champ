package sol_examples.simple_shooter;

import org.joml.Vector2f;
import sol_engine.engine_interface.SimulationLoop;
import sol_engine.engine_interface.SolSimulation;
import sol_engine.graphics_module.GraphicsModule;
import sol_engine.graphics_module.GraphicsModuleConfig;
import sol_engine.graphics_module.RenderConfig;
import sol_engine.graphics_module.WindowConfig;
import sol_engine.input_module.InputModule;
import sol_engine.input_module.InputModuleConfig;
import sol_engine.loaders.WorldLoader;

public class SimpleShooterFromConfig extends SolSimulation {

    public static void main(String... args) {
        System.out.println("Simple shooter");

        SimulationLoop game = new SimulationLoop(new SimpleShooterFromConfig());
        game.start();
    }

    private float worldWidth = 1600;
    private float worldHeight = 900;

    @Override
    protected void onSetupModules() {
        modulesHandler.addModule(new GraphicsModule(
                new GraphicsModuleConfig(
                        new WindowConfig(0.5f, 0.5f, "Hello SOL", false),
//                        new WindowConfig(0.7f, 0.7f, "Hello SOL", false),
                        new RenderConfig(worldWidth / 2, worldHeight / 2, worldWidth, worldHeight)
                )
        ));

        modulesHandler.addModule(new InputModule(new InputModuleConfig(
                new Vector2f(worldWidth, worldHeight)
        )));
    }

    @Override
    protected void onSetupWorld() {
        WorldLoader worldLoader = new WorldLoader();
        worldLoader.loadIntoWorld(world, "simple_shooter_resources/world_config.json");
    }
}
