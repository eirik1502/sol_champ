package sol_engine.utils;

import java.util.List;
import java.util.function.IntConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Repeat {
    public static <T> void repeat(int repeats, IntConsumer consumer) {
        IntStream.range(0, repeats).forEach(consumer);
    }

    public static <T> List<T> listConstructor(int repeats, Function.OneArgReturn<Integer, T> mapping) {
        return IntStream.range(0, repeats).mapToObj(mapping::invoke).collect(Collectors.toList());
    }
}
