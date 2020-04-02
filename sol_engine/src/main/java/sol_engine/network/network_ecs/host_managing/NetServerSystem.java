package sol_engine.network.network_ecs.host_managing;

import org.joml.Vector2f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sol_engine.core.ModuleSystemBase;
import sol_engine.core.TransformComp;
import sol_engine.ecs.Entity;
import sol_engine.network.network_ecs.EntityHost;
import sol_engine.network.network_ecs.packets.HostConnectedPacket;
import sol_engine.network.network_game.GameHost;
import sol_engine.network.network_sol_module.NetworkServerModule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NetServerSystem extends ModuleSystemBase {
    private final Logger logger = LoggerFactory.getLogger(NetServerSystem.class);

    private Map<Entity, EntityHost> entityHosts = new HashMap<>();

    @Override
    protected void onSetup() {
        usingComponents(NetServerComp.class);
        usingModules(NetworkServerModule.class);
    }

    @Override
    public void onStart() {
        NetworkServerModule serverModule = getModule(NetworkServerModule.class);
        serverModule.usePacketTypes(HostConnectedPacket.class);

        forEachWithComponents(NetServerComp.class, (entity, netServerComp) -> {
            System.out.println(entity.name);
            if (netServerComp.staticConnectionPacket != null) {
                System.out.println("Net packet:");
                System.out.println(netServerComp.staticConnectionPacket.getClass());
                System.out.println(netServerComp.staticConnectionPacket);
                serverModule.usePacketTypes(netServerComp.staticConnectionPacket.getClass());
            }
        });
    }

    @Override
    protected void onUpdate() {
        NetworkServerModule serverMod = getModule(NetworkServerModule.class);

        if (entities.size() != 1) {
            logger.warn("There should only be one entity with NetServerComp, current count: " + entities.size());
        }

        forEachWithComponents(NetServerComp.class, (entity, netServerComp) -> {

            handleNewHosts(serverMod, netServerComp);

            //TODO: handle disconnecting hosts
        });
    }

    private void handleNewHosts(NetworkServerModule serverModule, NetServerComp netServerComp) {
        serverModule.getNewConnectedHosts().forEach(newHost -> {
            EntityHostStartData newEntityData = netServerComp.hostEntitiesStartData
                    .get(newHost.teamIndex).get(newHost.playerIndex);
            String entityClass = newEntityData.entityClass;
            Vector2f startPos = newEntityData.startPos;
            Entity newHostEntity = NetEcsUtils.addEntityForHost(
                    true,
                    newHost,
                    entityClass,
                    startPos,
                    world
            );

            if (netServerComp.staticConnectionPacket != null) {
                // reply with the static connect packet
                serverModule.sendPacket(netServerComp.staticConnectionPacket, newHost);
            }

            // send the new client to all previously connected hosts
            // the new host is not present in this map yet
            HostConnectedPacket newHostPacket = new HostConnectedPacket(
                    newHost,
                    entityClass,
                    startPos
            );
            List<GameHost> connectedHosts = entityHosts.values().stream()
                    .map(entityHost -> entityHost.host)
                    .collect(Collectors.toList());
            serverModule.sendPacket(newHostPacket, connectedHosts);

            // add the new host
            entityHosts.put(newHostEntity, new EntityHost(newHostEntity, newHost, entityClass));

            // send all connected hosts to the new client
            entityHosts.values().forEach(entityHost ->
                    serverModule.sendPacket(new HostConnectedPacket(
                                    entityHost.host,
                                    entityHost.entityClass,
                                    entityHost.entity.getComponent(TransformComp.class).position
                            ),
                            newHost
                    ));
        });
    }
}
