package sol_engine.archive.network_module.network_ecs;

import sol_engine.archive.network_module.NetInPacket;
import sol_engine.archive.network_module.NetOutPacket;

public abstract class NetWorldPacket {

    public int netId;
    public String compType;


    public static class In extends NetWorldPacket {
        public NetInPacket packetData;
    }

    public static class Out extends NetWorldPacket {
        public NetOutPacket packetData;
    }

}
