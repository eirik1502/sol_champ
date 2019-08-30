package sol_engine.network_module.network_ecs;

import sol_engine.core.TransformComp;
import sol_engine.network_module.NetInPacket;
import sol_engine.network_module.NetOutPacket;

public class NetEcsUtil {

    public static NetOutPacket transformToPacket(TransformComp transComp) {
        return new NetOutPacket()
                .writeFloat(transComp.x)
                .writeFloat(transComp.y)
                .writeFloat(transComp.rotZ)
                .writeFloat(transComp.scaleX)
                .writeFloat(transComp.scaleY);
    }

    public static void packetToTransform(NetInPacket packet, TransformComp transformComp) {
        transformComp.x = packet.readFloat();
        transformComp.y = packet.readFloat();
        transformComp.rotZ = packet.readFloat();
        transformComp.scaleX = packet.readFloat();
        transformComp.scaleY = packet.readFloat();
    }
}
