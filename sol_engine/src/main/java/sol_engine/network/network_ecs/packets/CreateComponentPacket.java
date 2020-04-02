package sol_engine.network.network_ecs.packets;

import sol_engine.ecs.Component;
import sol_engine.network.packet_handling.NetworkPacket;

public class CreateComponentPacket implements NetworkPacket {
    public int netId;
    public Component component;
}
