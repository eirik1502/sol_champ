package sol_engine.utils.collections;

import java.util.function.Consumer;
import java.util.stream.Stream;

public class EqualTypedPair<T> {

    private T elem1, elem2;


    public EqualTypedPair(T elem1, T elem2) {
        this.elem1 = elem1;
        this.elem2 = elem2;
    }

    public T getFirst() {
        return elem1;
    }

    public T getLast() {
        return elem2;
    }

    public Stream<T> stream() {
        return Stream.of(elem1, elem2);
    }

    public void forEach(Consumer<T> consumer) {
        stream().forEach(consumer);
    }

}
