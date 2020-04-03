package sol_engine.network.network_ecs.world_syncing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sol_engine.core.ModuleSystemBase;
import sol_engine.core.TransformComp;
import sol_engine.ecs.Component;
import sol_engine.ecs.Entity;
import sol_engine.network.network_ecs.host_managing.NetIdComp;
import sol_engine.network.network_ecs.packets.NetTransformPacket;
import sol_engine.network.network_ecs.packets.UpdateComponentPacket;
import sol_engine.network.network_sol_module.NetworkServerModule;

import java.util.Set;
import java.util.function.Function;

public class NetSyncServerSystem extends ModuleSystemBase {
    private static Logger logger = LoggerFactory.getLogger(NetSyncServerSystem.class);

    @Override
    protected void onSetup() {
        usingComponents(NetIdComp.class, NetSyncComp.class);
        usingModules(NetworkServerModule.class);
    }

    @Override
    protected void onSetupEnd() {
        getModule(NetworkServerModule.class).usePacketTypes(UpdateComponentPacket.class);
    }

    @Override
    protected void onUpdate() {
        NetworkServerModule serverModule = getModule(NetworkServerModule.class);
        forEachWithComponents(
                NetIdComp.class,
                NetSyncComp.class,
                (entity, netIdComp, netSyncComp) -> {
                    syncComponents(netIdComp.id, entity, netSyncComp.syncComponentTypes, serverModule);
                });
    }

    private void syncComponents(
            int netId,
            Entity entity,
            Set<Class<? extends Component>> syncComponentTypes,
            NetworkServerModule serverModule
    ) {
        syncComponentTypes.stream()
                .filter(compType -> {
                    if (entity.hasComponent(compType)) {
                        return true;
                    } else {
                        logger.warn("Trying to sync a component that is not present in entity. CompType: " + compType + ", entity: " + entity);
                        return false;
                    }
                })
                .map(entity::getComponent)
                .forEach(comp -> {
                    serverModule.sendPacketAll(new UpdateComponentPacket(netId, comp));
                });
    }
}
