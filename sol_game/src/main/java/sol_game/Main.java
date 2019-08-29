package sol_game;

import sol_engine.engine_interface.ThreadedSimulationLoop;
import sol_game.sol_client.ClientSimulation;

public class Main {

//    public static class TestSimulation extends SolSimulation {
//
//        @Override
//        protected void setup() {
//            float worldWidth = 1600;
//            float worldHeight = 900;
//
//
//            modulesHandler.addModule(new GraphicsModule(
//                    new GraphicsModuleConfig(
//                            new WindowConfig(0.3f, 0.3f, "Hello SOL"),
//                            new RenderConfig(worldWidth/2, worldHeight/2, worldWidth, worldHeight)
//                    )
//            ));
//
//
////            world.addSystem(sol_game.MoveCircularSystem.class);
//            world.addSystem(RenderSystem.class);
//
//
//            world.addEntityClass(new EntityClass("rect").addBaseComponents(
//                    new TransformComp(100, 100),
//                    new RenderSquareComp(new Square( 100, 100,100, 100, MattMaterial.RED)),
//                    new MoveCircularComp(800-350, 100, 50)
//            ));
//
//            world.instanciateEntityClass("rect", "rect1")
//                    .getComponent(TransformComp.class).setXY(300, 300);
//
//            world.instanciateEntityClass("rect", "rect2")
//                    .getComponent(TransformComp.class).setXY(400, 300);
//
//            System.out.println(
//                    world.getEntityByName("rect1").getComponent(TransformComp.class)
//                    ==
//                    world.getEntityByName("rect2").getComponent(TransformComp.class)
//            );
//
//            System.out.println(world.getEntityByName("rect2").getComponent(TransformComp.class).x);
//
//        }
//    }

    public static void main(String... args) {

        ThreadedSimulationLoop clientLoop = new ThreadedSimulationLoop(new ClientSimulation());
//        ThreadedSimulationLoop serverLoop = new ThreadedSimulationLoop(new ServerSimulation());

//        serverLoop.startListening();
        clientLoop.start();

        try {
            clientLoop.join();
//            serverLoop.join();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }


//        SolSimulation sim = new TestSimulation();
//        SimulationLoop simLoop = new SimulationLoop(sim, 0.16f / 4);
//        simLoop.startListening();


//        sim.startListening();


//        float time = 0;
//        while(time < 60 * 60) {
//
//            sim.step();
//
//
//            time += 1;
//
//            try {
//                Thread.sleep(16);
//            } catch (InterruptedException e1) {
//                e1.printStackTrace();
//            }
//        }

    }

    private static void worldLoader() {
        //        World world = new World();
//        WorldLoaderOld.loadIntoWorld(world, "worldConfig.json");
    }

//    private static void simpleTest() {
//        ModulesHandler modulesHandler = new ModulesHandler();
//
//        float worldWidth = 1600;
//        float worldHeight = 900;
//        GraphicsModuleConfig graphicsConfig = new GraphicsModuleConfig(
//                new WindowConfig(0.3f, 0.3f, "Hello SOL"),
//                new RenderConfig(worldWidth/2, worldHeight/2, worldWidth, worldHeight)
//        );
//
//        modulesHandler.addModule(new GraphicsModule(graphicsConfig));
//
//
//
//        World world = new World(modulesHandler);
//
//        world.addSystem(RenderSystem.class);
//        world.addSystem(sol_game.MoveCircularSystem.class);
//
//        Entity e = world.createEntity("rect1");
//        e.addComponent(new TransformComp(100, 100));
//        e.addComponent(new RenderSquareComp(new Square( 100, 100,100, 100, MattMaterial.RED)));
//        e.addComponent(new sol_game.MoveCircularComp(800-350, 100, 50));
//        world.addEntity(e);
//
//        float time = 0;
//        while(time < 60 * 60) {
//
//            world.update();
//
//
//            time += 1;
//
//            try {
//                Thread.sleep(16);
//            } catch (InterruptedException e1) {
//                e1.printStackTrace();
//            }
//        }
//    }

//    private static void runGraphics() {
//        Window window = new Window(0.8f, 0.8f);
//        Renderer renderer = new Renderer(window.getRenderingContext());
//
//
//        Square cs = new Square( 100, 100,700, 700, MattMaterial.RED);
//        Square cs2 = new Square( 300, 300,300, 300, new MattMaterial(new Color(0.7f,0, 0.7f)));
//        renderer.addColoredSquare(cs);
//        renderer.addColoredSquare(cs2);
//
//        float time = 0;
//        while(time < 60 * 60) {
//
//
//            window.pollEvents();
//            renderer.render();
//            cs.x = (float) (800-350 + Math.cos(time/10) * 400);
//            cs.y = (float)(100 + Math.sin(time/10) * 100);
//
//            cs2.x = 650 + (float)Math.sin(time/100) * 600;
//
//            time += 1;
//
//            try {
//                Thread.sleep(16);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//    }
}