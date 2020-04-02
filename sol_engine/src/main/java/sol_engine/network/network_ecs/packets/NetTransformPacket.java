package sol_engine.network.network_ecs.packets;

import org.joml.Vector2f;
import sol_engine.core.TransformComp;
import sol_engine.network.packet_handling.NetworkPacket;

public class NetTransformPacket implements NetworkPacket {
    public Vector2f position = new Vector2f();
    public Vector2f scale = new Vector2f();
    public float rotationZ = 0;

    private NetTransformPacket() {
    }

    public NetTransformPacket(TransformComp transComp) {
        fromTransformComp(transComp);
    }

    public void fromTransformComp(TransformComp transComp) {
        position.set(transComp.position);
        scale.set(transComp.scale);
        rotationZ = transComp.rotationZ;
    }

    public void toTransformComp(TransformComp transComp) {
        transComp.position.set(position);
        transComp.scale.set(scale);
        transComp.rotationZ = rotationZ;
    }
}
