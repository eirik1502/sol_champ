package sol_engine.network.packet_handling;

import java.util.Deque;

public interface NetworkRawPacketLayer {
    Deque<NetworkPacketRaw> pollPackets();

    void pushPacket(String packet);

    boolean isConnected();

    void terminate();
}
