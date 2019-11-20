package sol_engine.utils;

import java.util.function.IntConsumer;
import java.util.stream.IntStream;

public class Repeat {
    public static <T> void repeat(int repeats, IntConsumer consumer) {
        IntStream.range(0, repeats).forEach(consumer);
    }
}
