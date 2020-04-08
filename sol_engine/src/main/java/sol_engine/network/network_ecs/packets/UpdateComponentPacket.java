package sol_engine.network.network_ecs.packets;

import sol_engine.ecs.Component;
import sol_engine.network.packet_handling.NetworkPacket;

public class UpdateComponentPacket implements NetworkPacket {
    public int netId;
    public Component component;


    public UpdateComponentPacket() {
    }

    public UpdateComponentPacket(int netId, Component component) {
        this.netId = netId;
        this.component = component;
    }

    @Override
    public String toString() {
        return "UpdateComponentPacket{" +
                "netId=" + netId +
                ", component=" + component +
                '}';
    }
}
