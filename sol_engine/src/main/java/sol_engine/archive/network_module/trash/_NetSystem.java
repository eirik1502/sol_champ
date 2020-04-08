package sol_engine.archive.network_module.network_ecs;

import sol_engine.core.ModuleSystemBase;
import sol_engine.core.TransformComp;
import sol_engine.ecs.Entity;
import sol_engine.ecs.EntityClassInstanciateListener;
import sol_engine.archive.network_module.NetChannel;
import sol_engine.archive.network_module.network_modules.NetworkModule;

public class _NetSystem extends ModuleSystemBase implements EntityClassInstanciateListener {

    private static final int NET_CHANNEL_ID = 1;

    private NetChannel netChannel;

    public void broadcastTransform(NetIdComp netIdComp, TransformComp transform) {
        NetWorldPacket.Out packet = new NetWorldPacket.Out();
        packet.netId = netIdComp.id;
        packet.compType = transform.getClass().getSimpleName();
        packet.packetData = NetEcsUtil.transformToPacket(transform);
    }

    @Override
    public void onStart() {
        usingModules(NetworkModule.class);

        usingComponents(NetIdComp.class, TransformComp.class);

        world.addEntityClassInstanciatListener(this);

        NetworkModule netModule = getModule(NetworkModule.class);
        netModule.registerPacket(EntityClassInstanciatedPacket.class);

        if (!netModule.isServer()) {
            netModule.registerPacketListener(EntityClassInstanciatedPacket.class, (host, packet) ->
                    System.out.println("Client got ec inst: " + packet.entityClass));
        }
    }

    @Override
    public void onUpdate() {
        groupEntities.forEach(e -> {

            TransformComp transComp = e.getComponent(TransformComp.class);


        });
    }

    @Override
    public void onEnd() {
        world.removeEntityClassInstanciatListener(this);
    }

    @Override
    public void onEntityClassInstanciated(String className, Entity entity) {
        NetworkModule netModule = getModule(NetworkModule.class);

        if (netModule.isServer()) {
            netModule.sendToAll(new EntityClassInstanciatedPacket(className));
        }
    }


}
