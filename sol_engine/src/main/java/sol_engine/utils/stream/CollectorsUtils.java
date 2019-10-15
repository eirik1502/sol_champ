package sol_engine.utils.stream;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.IntStream;

public class CollectorsUtils {

    public static Collector<Float, List<Float>, float[]> toFloatArray() {
        return Collector.of(
                ArrayList::new,
                List::add,
                (left, right) -> {
                    left.addAll(right);
                    return left;
                },
                list -> {
                    float[] arr = new float[list.size()];
                    IntStream.range(0, list.size()).forEach(i -> arr[i] = list.get(i));
                    return arr;
                });
    }

    public static Collector<Byte, List<Byte>, byte[]> toByteArray() {
        return Collector.of(
                ArrayList::new,
                List::add,
                (left, right) -> {
                    left.addAll(right);
                    return left;
                },
                list -> {
                    byte[] arr = new byte[list.size()];
                    IntStream.range(0, list.size()).forEach(i -> arr[i] = list.get(i));
                    return arr;
                });
    }
}
