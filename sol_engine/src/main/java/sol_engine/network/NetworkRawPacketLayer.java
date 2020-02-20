package sol_engine.network;

import java.util.Deque;

public interface NetworkRawPacketLayer {
    Deque<NetworkPacketRaw> pollPackets();

    void pushPacket(String packet);
}
