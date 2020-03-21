package sol_engine.network.network_game;

import sol_engine.network.packet_handling.NetworkPacket;
import sol_engine.utils.collections.Pair;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * This queue represents queues for each host for a given packet type
 *
 * @param <T> The packet type
 */
public class PacketsQueueByHost<T extends NetworkPacket> {

    private Map<GameHost, Deque<T>> queueByHost = new HashMap<>();


    public PacketsQueueByHost() {
    }

    public PacketsQueueByHost(PacketsQueueByHost<T> copyOf) {
        addAll(copyOf);
    }

    public void addAll(PacketsQueueByHost<T> source) {
        source.queueByHost
                .forEach((host, packetQueue) ->
                        packetQueue.forEach(packet ->
                                add(host, packet)
                        )
                );
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

    public Stream<Pair<GameHost, Deque<T>>> stream() {
        return queueByHost.entrySet().stream()
                .map(entry -> new Pair<>(entry.getKey(), entry.getValue()));
    }

    public Stream<Pair<GameHost, T>> streamAllPacketsWithHost() {
        return stream()
                .flatMap(entry -> entry.getLast().stream()
                        .map(packet -> new Pair<>(entry.getFirst(), packet))
                );
    }

    public void forEach(BiConsumer<GameHost, Deque<T>> consumer) {
        stream().forEach(entry -> consumer.accept(entry.getFirst(), entry.getLast()));
    }

    public void forEachPacketWithHost(BiConsumer<GameHost, T> consumer) {
        streamAllPacketsWithHost().forEach(entry -> consumer.accept(entry.getFirst(), entry.getLast()));
    }

    public static <T extends NetworkPacket> Collector<Pair<GameHost, T>, ?, PacketsQueueByHost<T>> pairCollector() {
        return Collector.of(
                PacketsQueueByHost::new,
                (queue, entry) -> queue.add(entry.getFirst(), entry.getLast()),
                (queue1, queue2) -> {
                    queue1.addAll(queue2);
                    return queue1;
                }
        );
    }
}
