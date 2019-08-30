package sol_engine.network_module.connection_handler;

import sol_engine.network_module.NetPacket;

public interface PacketListener <T extends NetPacket> {

    void onPacket(ConnectedHost fromHost, T packet);
}
