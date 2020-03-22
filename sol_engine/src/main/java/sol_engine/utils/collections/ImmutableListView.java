package sol_engine.utils.collections;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class ImmutableListView<T> implements Iterable<T> {

    private List<T> list;

    public ImmutableListView(List<T> forList) {
        this.list = forList;
    }

    public int size() {
        return list.size();
    }

    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }

    public Stream<T> stream() {
        return list.stream();
    }

    public List<T> copyToList() {
        return copyToList(new ArrayList<>());
    }

    public List<T> copyToList(List<T> copyTo) {
        copyTo.addAll(this.list);
        return copyTo;
    }

    public boolean equals(Object o) {
        if (o instanceof List) {
            return list.equals(o);
        } else if (o instanceof ImmutableListView) {
            return list.equals(((ImmutableListView) o).list);
        }
        return false;
    }
}
