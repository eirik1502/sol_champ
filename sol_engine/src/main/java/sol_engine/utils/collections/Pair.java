package sol_engine.utils.collections;

public class Pair<T, K> {
    private T elem1;
    private K elem2;


    public Pair(T elem1, K elem2) {
        this.elem1 = elem1;
        this.elem2 = elem2;
    }

    public T getFirst() {
        return elem1;
    }

    public K getLast() {
        return elem2;
    }
}
