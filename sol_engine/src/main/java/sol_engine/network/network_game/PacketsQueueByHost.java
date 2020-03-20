package sol_engine.network.network_game;

import sol_engine.network.packet_handling.NetworkPacket;

import java.util.*;
import java.util.stream.Collectors;

public class PacketsQueueByHost<T extends NetworkPacket> {

    private Map<GameHost, Deque<T>> queueByHost = new HashMap<>();


    public PacketsQueueByHost() {
    }

    public PacketsQueueByHost(PacketsQueueByHost<T> copyOf) {
        queueByHost = copyOf.queueByHost.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> new ArrayDeque<>(entry.getValue())
                ));
    }

    public int totalPacketCount() {
        return peekAllPackets().size();
    }

    public void add(GameHost host, T packet) {
        queueByHost.computeIfAbsent(host, key -> new ArrayDeque<>()).add(packet);
    }

    public void clear() {
        queueByHost.clear();
    }

    public T peek(GameHost host) {
        return queueByHost.get(host).peek();
    }

    public Deque<T> peekAllPacketsForHost(GameHost host) {
        return new ArrayDeque<>(queueByHost.get(host));
    }

    public List<NetworkPacket> peekAllPackets() {
        return queueByHost.values().stream()
                .flatMap(Deque::stream)
                .collect(Collectors.toList());
    }
}
