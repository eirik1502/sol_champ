package sol_engine.network.network_input;

public class NetworkInputSourceModuleConfig {
    Class<? extends NetInputPacket> inputPacketType;

    public NetworkInputSourceModuleConfig() {
    }

    public NetworkInputSourceModuleConfig(Class<? extends NetInputPacket> inputPacketType) {
        this.inputPacketType = inputPacketType;
    }
}
