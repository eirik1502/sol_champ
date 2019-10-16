package sol_engine.utils.stream;

import sol_engine.utils.mutable_primitives.MInt;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public class WithIndex {
    public static <T> Consumer<T> consumer(BiConsumer<T, Integer> consumer) {
        return consumer(0, consumer);
    }

    public static <T> Consumer<T> consumer(int startIndex, BiConsumer<T, Integer> consumer) {
        MInt index = new MInt(startIndex);
        return val -> consumer.accept(val, index.value++);
    }

    public static <T> Function<T, ValueWithIndex<T>> map() {
        return map(0);
    }

    public static <T> Function<T, ValueWithIndex<T>> map(int startIndex) {
        MInt index = new MInt(startIndex);
        return val -> new ValueWithIndex<>(val, index.value++);
    }

    public static <T> Stream<ValueWithIndex<T>> stream(Stream<T> stream) {
        return stream(stream, 0);
    }

    public static <T> Stream<ValueWithIndex<T>> stream(Stream<T> stream, int startIndex) {
        MInt index = new MInt(startIndex);
        return stream.map(val -> new ValueWithIndex<T>(val, index.value++));
    }
}
