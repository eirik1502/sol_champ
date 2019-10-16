package sol_engine.utils.stream;

public class ValueWithIndex<T> {
    public T value;
    public int i;

    public ValueWithIndex(T value, int i) {
        this.value = value;
        this.i = i;
    }
}
