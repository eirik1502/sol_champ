package sol_engine.network_module.network_ecs;

import sol_engine.core.ModuleSystemBase;
import sol_engine.core.TransformComp;
import sol_engine.network_module.network_modules.NetworkServerModule;

public class NetServerSystem extends ModuleSystemBase {


    public void broadcastTransform(NetIdComp netIdComp, TransformComp transform) {
        NetWorldPacket.Out packet = new NetWorldPacket.Out();
        packet.netId = netIdComp.id;
        packet.compType = transform.getClass().getSimpleName();
        packet.packetData = NetEcsUtil.transformToPacket(transform);
    }

    @Override
    public void onStart() {
        usingModules(NetworkServerModule.class);

        usingComponents(NetIdComp.class, TransformComp.class);
    }

    @Override
    public void onUpdate() {
        groupEntities.forEach(e -> {

            TransformComp transComp = e.getComponent(TransformComp.class);


        });
    }

    @Override
    public void onEnd() {

    }


}
