package sol_game.sol_server;

import sol_engine.core.TransformComp;
import sol_engine.ecs.EntityClass;
import sol_engine.engine_interface.SolSimulation;
import sol_engine.network_module.network_modules.NetworkServerModule;
import sol_game.MoveCircularComp;

public class ServerSimulation extends SolSimulation {


    @Override
    protected void setup() {
        NetworkServerModule.Config netConfig = new NetworkServerModule.Config();
        netConfig.port = 7779;

        modulesHandler.addModule(new NetworkServerModule(netConfig));

//        world.addSystem(_NetSystem.class);

        world.addEntityClass(new EntityClass("rect").addBaseComponents(
                new TransformComp(100, 100),
                new MoveCircularComp(800 - 350, 100, 50)
        ));

        world.instanciateEntityClass("rect", "rect1")
                .getComponent(TransformComp.class).setXY(300, 300);

        world.instanciateEntityClass("rect", "rect2")
                .modifyComponent(TransformComp.class, c -> c.setXY(600, 300))
                .modifyComponent(MoveCircularComp.class, c -> {
                    c.centerX += 300;
                    c.centerY += 300;
                });
    }
}
