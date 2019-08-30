package sol_engine.utils;

import java.util.*;
import java.util.stream.Stream;

public class ImmutableSetView<T>  implements Iterable<T>{

    private Set<T> set;

    public ImmutableSetView(Set<T> forList) {
        this.set = forList;
    }

    public int size() {
        return set.size();
    }

    @Override
    public Iterator<T> iterator() {
        return set.iterator();
    }

    public Stream<T> stream() {
        return set.stream();
    }

    public Set<T> asSet() {
        return new HashSet<>(set);
    }

    public boolean equals(Object o) {
        if (o instanceof Set) {
            return set.equals(o);
        }
        else if (o instanceof ImmutableSetView) {
            return set.equals(((ImmutableSetView) o).set);
        }
        return false;
    }
}
