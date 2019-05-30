package game;


import sol_engine.ecs.World;
import sol_engine.loaders.WorldLoaderOld;

public class Main {
    public static void main(String... args) {

        World world = new World();
        WorldLoaderOld.loadIntoWorld(world, "worldConfig.json");
    }
}