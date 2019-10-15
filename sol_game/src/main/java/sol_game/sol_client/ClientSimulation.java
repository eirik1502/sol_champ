package sol_game.sol_client;

import sol_engine.core.TransformComp;
import sol_engine.ecs.EntityClass;
import sol_engine.engine_interface.SolSimulation;
import sol_engine.graphics_module.*;
import sol_engine.graphics_module.materials.MattMaterial;
import sol_engine.network_module.network_modules.NetworkClientModule;
import sol_game.MoveCircularComp;

public class ClientSimulation extends SolSimulation {
    @Override
    protected void setup() {
        float worldWidth = 1600;
        float worldHeight = 900;


        modulesHandler.addModule(new GraphicsModule(
                new GraphicsModuleConfig(
                        new WindowConfig(0.3f, 0.3f, "Hello SOL"),
                        new RenderConfig(worldWidth / 2, worldHeight / 2, worldWidth, worldHeight)
                )
        ));

        NetworkClientModule.Config netConfig = new NetworkClientModule.Config();
        netConfig.port = 7779;
        netConfig.serverAddr = "localhost";

        modulesHandler.addModule(new NetworkClientModule(netConfig));


//        world.addSystem(sol_game.MoveCircularSystem.class);
        world.addSystem(RenderSystem.class);
//        world.addSystem(_NetSystem.class);


        world.addEntityClass(new EntityClass("rect").addBaseComponents(
                new TransformComp(100, 100),
                new RenderShapeComp(100, 100, MattMaterial.RED),
                new MoveCircularComp(800 - 350, 100, 50)
        ));

//        world.instanciateEntityClass("rect", "rect1")
//                .getComponent(TransformComp.class).setXY(300, 300);
//
//        world.instanciateEntityClass("rect", "rect2")
//                .modifyComponent(TransformComp.class, c -> c.setXY(600, 300))
//                .modifyComponent(RenderSquareComp.class, c -> c.material = MattMaterial.GREEN)
//                .modifyComponent(MoveCircularComp.class, c -> {
//                    c.centerX += 300;
//                    c.centerY += 300;
//                });

    }
}
