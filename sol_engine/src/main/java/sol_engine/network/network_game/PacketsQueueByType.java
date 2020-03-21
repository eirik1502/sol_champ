package sol_engine.network.network_game;

import sol_engine.network.packet_handling.NetworkPacket;

import java.util.*;
import java.util.stream.Collectors;

public class PacketsQueueByType {


    private Map<Class<? extends NetworkPacket>, Deque<NetworkPacket>> queueByType = new HashMap<>();


    public PacketsQueueByType() {
    }

    public PacketsQueueByType(PacketsQueueByType copyOf) {
        queueByType = copyOf.queueByType.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> new ArrayDeque<>(entry.getValue())
                ));
    }

    public int totalPacketCount() {
        return peekAllPackets().size();
    }

    public void add(NetworkPacket packet) {
        queueByType.computeIfAbsent(packet.getClass(), key -> new ArrayDeque<>()).add(packet);
    }

    public void clear() {
        queueByType.clear();
    }

    @SuppressWarnings("unchecked")
    public <T extends NetworkPacket> T peek(Class<T> type) {
        return (T) queueByType.get(type).peek();
    }

    @SuppressWarnings("unchecked")
    public <T extends NetworkPacket> Deque<T> peekAll(Class<T> type) {
        return (Deque<T>) new ArrayDeque<>(queueByType.get(type));
    }

    public List<NetworkPacket> peekAllPackets() {
        return queueByType.values().stream()
                .flatMap(Deque::stream)
                .collect(Collectors.toList());
    }
}
