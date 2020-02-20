package sol_engine.network;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class NetworkClassPacketLayer {

    // The keys is also a collection of Packet types in use
    private Map<Class<? extends NetworkPacket>, ArrayDeque<NetworkPacket>> pendingPackets = new HashMap<>();

    private NetworkRawPacketLayer rawPacketLayer;


    public NetworkClassPacketLayer(NetworkRawPacketLayer rawPacketLayer) {
        this.rawPacketLayer = rawPacketLayer;
    }

    public void usePacketTypes(Class<? extends NetworkPacket>... packetTypes) {
        Arrays.stream(packetTypes).forEach(packetType -> pendingPackets.put(packetType, new ArrayDeque<>()));
    }

    @SuppressWarnings("unchecked")
    public <T extends NetworkPacket> ArrayDeque<T> pollPackets(Class<T> packetType) {
        if (!pendingPackets.containsKey(packetType)) {
            throw new IllegalArgumentException("Trying to poll packets of a type that is not beeing used");
        }
        ArrayDeque<NetworkPacket> pendingPacketsOfType = pendingPackets.get(packetType);
        ArrayDeque<T> packets = new ArrayDeque<>((ArrayDeque<T>) pendingPacketsOfType);
        pendingPacketsOfType.clear();
        return packets;
    }

    public void pushPacket(NetworkPacket packet) {
        if (!pendingPackets.containsKey(packet.getClass())) {
            throw new IllegalArgumentException("Trying to push a packet of a type that is not beeing used");
        }

    }
}
