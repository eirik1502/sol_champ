package sol_engine.network.network_ecs;

import org.joml.Vector2f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sol_engine.core.ModuleSystemBase;
import sol_engine.core.TransformComp;
import sol_engine.input_module.InputComp;
import sol_engine.loaders.LoadersLogger;
import sol_engine.network.network_sol_module.NetworkServerModule;
import sol_engine.network.network_ecs.NetIdComp;
import sol_engine.utils.collections.Pair;

public class NetServerSystem extends ModuleSystemBase {
    private final Logger logger = LoggerFactory.getLogger(NetServerSystem.class);

    @Override
    protected void onSetup() {
        usingComponents(NetServerComp.class);
        usingModules(NetworkServerModule.class);
    }

    @Override
    public void onStart() {
    }

    @Override
    protected void onUpdate() {
        NetworkServerModule serverMod = getModule(NetworkServerModule.class);

        if (entities.size() != 1) {
            logger.warn("There should only be one entity with NetServerComp, current count: " + entities.size());
        }

        forEachWithComponents(NetServerComp.class, (entity, netServerComp) -> {
            serverMod.getNewConnectedHosts().forEach(newHost -> {
                Pair<String, Vector2f> newEntityData = netServerComp.hostEntitiesStartData
                        .get(newHost.teamIndex).get(newHost.playerIndex);
                String eClass = newEntityData.getFirst();
                Vector2f startPos = newEntityData.getLast();
                String inputGroup = "t" + newHost.teamIndex + "p" + newHost.playerIndex;
                String name = eClass + "_" + newHost.name;

                world.instanciateEntityClass(eClass, name)
                        .addComponent(new NetIdComp(newHost.sessionId))
                        .modifyIfHasComponent(TransformComp.class, comp -> comp.position.set(startPos))
                        .modifyIfHasComponent(InputComp.class, comp -> comp.inputGroup = inputGroup);

                // let the client know its connected
                serverMod.sendPacket(new ServerConnectResponsePacket(), newHost);
            });
        });
    }
}
