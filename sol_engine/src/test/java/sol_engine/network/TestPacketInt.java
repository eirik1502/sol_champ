package sol_engine.network;

import sol_engine.network.packet_handling.NetworkPacket;

public class TestPacketInt implements NetworkPacket {
    public int value;

    public TestPacketInt(int value) {
        this.value = value;
    }
}
