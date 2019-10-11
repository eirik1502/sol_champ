package sol_engine.utils;

import java.util.*;

public class SetUtils {

    public static <T> Set<T> intersection(Set<T>... sets) {
        return intersection(Arrays.asList(sets));
    }

    public static <T> Set<T> intersection(Collection<Set<T>> sets) {
        if (sets.size() == 0) {
            return new HashSet<>();
        }
        if (sets.size() == 1) {
            return sets.iterator().next();
        }
        Iterator<Set<T>> it = sets.iterator();
        Set<T> intersection = new HashSet<>(it.next());
        while (it.hasNext()) {
            intersection.retainAll(it.next());
        }
        return intersection;
    }
}
