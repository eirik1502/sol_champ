package sol_engine.utils;

public abstract class AbstractCloneable<T> implements Cloneable {

    @SuppressWarnings("unchecked")
    public T clone() {
        try {
            return (T) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
