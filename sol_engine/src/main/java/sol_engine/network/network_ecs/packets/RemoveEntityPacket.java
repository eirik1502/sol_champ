package sol_engine.network.network_ecs.packets;

import sol_engine.network.packet_handling.NetworkPacket;

public class RemoveEntityPacket implements NetworkPacket {
    public int netId = -1;


    public RemoveEntityPacket() {
    }

    public RemoveEntityPacket(int netId) {
        this.netId = netId;
    }
}
