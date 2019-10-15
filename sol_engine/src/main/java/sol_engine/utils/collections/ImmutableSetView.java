package sol_engine.utils.collections;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Stream;

public class ImmutableSetView<T> implements Iterable<T> {

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

    public Set<T> copyToSet() {
        return copyToSet(new HashSet<>());
    }

    public Set<T> copyToSet(Set<T> copyTo) {
        copyTo.addAll(set);
        return copyTo;
    }

    public boolean equals(Object o) {
        if (o instanceof Set) {
            return set.equals(o);
        } else if (o instanceof ImmutableSetView) {
            return set.equals(((ImmutableSetView) o).set);
        }
        return false;
    }
}
