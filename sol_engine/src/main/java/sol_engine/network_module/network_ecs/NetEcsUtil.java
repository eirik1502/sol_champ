package sol_engine.network_module.network_ecs;

import sol_engine.core.TransformComp;
import sol_engine.network_module.NetInPacket;
import sol_engine.network_module.NetOutPacket;

public class NetEcsUtil {

    public static NetOutPacket transformToPacket(TransformComp transComp) {
        return new NetOutPacket()
                .writeFloat(transComp.position.x)
                .writeFloat(transComp.position.y)
                .writeFloat(transComp.rotationZ)
                .writeFloat(transComp.scale.x)
                .writeFloat(transComp.scale.y);
    }

    public static void packetToTransform(NetInPacket packet, TransformComp transformComp) {
        transformComp.position.x = packet.readFloat();
        transformComp.position.y = packet.readFloat();
        transformComp.rotationZ = packet.readFloat();
        transformComp.scale.x = packet.readFloat();
        transformComp.scale.y = packet.readFloat();
    }
}
