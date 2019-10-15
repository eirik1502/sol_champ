package sol_engine.utils.collections;

import java.util.*;
import java.util.function.Predicate;

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

    public static <T> EqualTypedPair<Set<T>> splitSet(Set<T> set, Predicate<T> predicate) {
        EqualTypedPair<Set<T>> results = new EqualTypedPair<>(new HashSet<T>(), new HashSet<T>());
        set.forEach(item -> {
            if (predicate.test(item)) {
                results.getFirst().add(item);
            } else {
                results.getLast().add(item);
            }
        });
        return results;
    }

    public static <T> void splitSet(Set<T> set, Set<T> matches, Set<T> remaining, Predicate<T> predicate) {
        set.forEach(item -> {
            if (predicate.test(item)) {
                matches.add(item);
            } else {
                remaining.add(item);
            }
        });
    }
}
