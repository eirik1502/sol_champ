package sol_engine.network_module;

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
