package sol_engine.network.network_ecs.host_managing;

import org.joml.Vector2f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sol_engine.core.ModuleSystemBase;
import sol_engine.core.TransformComp;
import sol_engine.ecs.Component;
import sol_engine.ecs.Entity;
import sol_engine.network.network_ecs.EntityHost;
import sol_engine.network.network_ecs.packets.CreateHostEntityPacket;
import sol_engine.network.network_ecs.packets.HostConnectedPacket;
import sol_engine.network.network_game.GameHost;
import sol_engine.network.network_sol_module.NetworkServerModule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NetServerSystem extends ModuleSystemBase {
    private final Logger logger = LoggerFactory.getLogger(NetServerSystem.class);
    private int nextEntityNetId = 0;

    private Map<Entity, EntityHost> entityHosts = new HashMap<>();

    @Override
    protected void onSetup() {
        usingComponents(NetServerComp.class);
        usingModules(NetworkServerModule.class);
    }

    @Override
    public void onStart() {
        NetworkServerModule serverModule = getModule(NetworkServerModule.class);
        serverModule.usePacketTypes(CreateHostEntityPacket.class);

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
            int newNetId = createEntityNetId();
            List<Class<? extends Component>> modifyComponentTypes = newEntityData.modifyComponents.stream()
                    .map(comp -> comp.getClass())
                    .collect(Collectors.toList());
            List<Component> modifyComponents = newEntityData.modifyComponents;
            Entity newHostEntity = NetEcsUtils.addEntityForHost(
                    true,
                    newHost,
                    newNetId,
                    entityClass,
                    modifyComponents,
                    world
            );

            if (netServerComp.staticConnectionPacket != null) {
                // reply with the static connect packet
                serverModule.sendPacket(netServerComp.staticConnectionPacket, newHost);
            }

            // send the new client to all previously connected hosts
            // the new host is not present in this map yet
            CreateHostEntityPacket createNewHostEntityPacket = new CreateHostEntityPacket(
                    newHost,
                    newNetId,
                    entityClass,
                    List.of(),
                    modifyComponents
            );
//            HostConnectedPacket newHostPacket = new HostConnectedPacket(
//                    newHost,
//                    entityClass,
//                    startPos
//            );
            List<GameHost> connectedHosts = entityHosts.values().stream()
                    .map(entityHost -> entityHost.host)
                    .collect(Collectors.toList());
            serverModule.sendPacket(createNewHostEntityPacket, connectedHosts);

            // add the new host
            entityHosts.put(newHostEntity, new EntityHost(newHostEntity, newHost, entityClass));

            // send all connected hosts to the new client, including itself.
            // World is not updated with the new component yet, but it is present in entityHosts
            entityHosts.values().forEach(entityHost -> {
//                HostConnectedPacket hostConnectedPacket = new HostConnectedPacket(
//                        entityHost.host,
//                        entityHost.entityClass,
//                        entityHost.entity.getComponent(TransformComp.class).position
//                );
                CreateHostEntityPacket createHostEntityPacket = new CreateHostEntityPacket(
                        entityHost.host,
                        entityHost.entity.getComponent(NetIdComp.class).id,
                        entityHost.entityClass,
                        List.of(),
                        modifyComponentTypes.stream()
                                .map(compType -> entityHost.entity.getComponent(compType))
                                .collect(Collectors.toList())
                );
                serverModule.sendPacket(
                        createHostEntityPacket,
                        newHost
                );
            });
        });
    }

    private int createEntityNetId() {
        return nextEntityNetId++;
    }
}
