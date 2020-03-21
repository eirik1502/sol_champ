package sol_engine.network.network_game;

import sol_engine.network.packet_handling.NetworkPacket;
import sol_engine.utils.collections.Pair;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PacketsQueue {
    private final Map<GameHost, Deque<NetworkPacket>> EMPTY_QUEUE_BY_HOST = new HashMap<>();
    private final Deque<NetworkPacket> EMPTY_QUEUE = new ArrayDeque<>();

    private Map<Class<? extends NetworkPacket>, Map<GameHost, Deque<NetworkPacket>>> queueByType
            = new HashMap<>();


    public PacketsQueue() {

    }

    public PacketsQueue(PacketsQueue copyOf) {
        addAll(copyOf);
    }

    public void addAll(PacketsQueue source) {
        source.queueByType.values()
                .forEach(queueByHost ->
                        queueByHost.forEach(this::addAll)
                );
    }

    public Deque<NetworkPacket> peekAllPackets() {
        return queueByType.values().stream()
                .flatMap(packetsByHost -> packetsByHost.values().stream()
                        .flatMap(Collection::stream)
                ).collect(Collectors.toCollection(ArrayDeque::new));
    }

    public int totalPacketCount() {
        return peekAllPackets().size();
    }

    private Class<? extends NetworkPacket> getType(NetworkPacket packet) {
        return packet.getClass();
    }

    public void add(GameHost host, NetworkPacket packet) {
        queueByType.computeIfAbsent(getType(packet), type_ -> new HashMap<>())
                .computeIfAbsent(host, queueByHost -> new ArrayDeque<>())
                .add(packet);
    }

    public void addAll(GameHost host, Collection<NetworkPacket> packets) {
        packets.forEach(packet -> add(host, packet));
    }

    public void clear() {
        // assuming the packet types are pretty consistent, we dont clear the packet types map
        // the hosts may however change over time, even though not likely
        queueByType.forEach((type, queueByHost) -> queueByHost.clear());
    }

    @SuppressWarnings("unchecked")
    public <T extends NetworkPacket> Deque<T> peek(Class<T> type, GameHost host) {
        Deque<NetworkPacket> queue = queueByType
                .getOrDefault(type, EMPTY_QUEUE_BY_HOST)
                .getOrDefault(host, EMPTY_QUEUE);
        return new ArrayDeque<>((Deque<T>) queue);
    }

    public <T extends NetworkPacket> Deque<T> pop(Class<T> type, GameHost host) {
        Deque<T> tempQueue = peek(type, host);
        queueByType.getOrDefault(type, EMPTY_QUEUE_BY_HOST).remove(host);
        return tempQueue;
    }


    public Map<Class<? extends NetworkPacket>, Deque<NetworkPacket>> peekForHost(GameHost host) {
        return queueByType.entrySet().stream()
                .filter(queueOfType -> queueOfType.getValue().containsKey(host))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        queueOfType -> new ArrayDeque<>(queueOfType.getValue().get(host))
                ));
    }

    public Map<Class<? extends NetworkPacket>, Deque<NetworkPacket>> popForHost(GameHost host) {
        Map<Class<? extends NetworkPacket>, Deque<NetworkPacket>> tempQueue = peekForHost(host);
        queueByType.forEach((type, queueByHost) -> {
            queueByHost.remove(host);
        });
        return tempQueue;
    }


    @SuppressWarnings("unchecked")
    public <T extends NetworkPacket> Map<GameHost, Deque<T>> peekOfType(Class<T> type) {
        return queueByType.getOrDefault(type, EMPTY_QUEUE_BY_HOST).entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        queueByHost -> new ArrayDeque<>((Deque<T>) queueByHost.getValue())
                ));
    }

    public <T extends NetworkPacket> Map<GameHost, Deque<T>> popOfType(Class<T> type) {
        Map<GameHost, Deque<T>> tempQueue = peekOfType(type);
        queueByType.getOrDefault(type, EMPTY_QUEUE_BY_HOST).clear();
        return tempQueue;
    }
}
