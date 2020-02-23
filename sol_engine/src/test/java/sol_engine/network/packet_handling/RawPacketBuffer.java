package sol_engine.network.packet_handling;

import sol_engine.network.packet_handling.NetworkPacketRaw;
import sol_engine.network.packet_handling.NetworkRawPacketLayer;
import sol_engine.network.server.Host;

import java.util.ArrayDeque;
import java.util.Deque;

public class RawPacketBuffer implements NetworkRawPacketLayer {

    private Deque<NetworkPacketRaw> packetBuffer = new ArrayDeque<>();

    @Override
    public Deque<NetworkPacketRaw> pollPackets() {
        Deque<NetworkPacketRaw> packets = new ArrayDeque<>(packetBuffer);
        packetBuffer.clear();
        return packets;
    }

    @Override
    public void pushPacket(String packet) {
        packetBuffer.add(new NetworkPacketRaw(new Host("", "", "", -1), packet));
    }
}
