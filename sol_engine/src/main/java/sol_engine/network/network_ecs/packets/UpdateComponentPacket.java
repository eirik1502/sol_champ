package sol_engine.network.network_ecs.packets;

import sol_engine.ecs.Component;

public class UpdateComponentPacket {
    public int netId;
    public Component component;
}
