package sol_engine.ecs;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class ComponentFamily {

    private Set<Class<? extends Component>> compTypes;

    @SafeVarargs
    public ComponentFamily(Class<? extends Component>... compTypes) {
        this(new HashSet<>(Arrays.asList(compTypes)));
    }
    public ComponentFamily(Set<Class<? extends Component>> compTypes) {
        this.compTypes = new HashSet<>(compTypes);
    }

    public Stream<Class<? extends Component>> stream() {
        return compTypes.stream();
    }

    public boolean contains(ComponentFamily other) {
        return compTypes.containsAll(other.compTypes);
    }

    @Override
    public boolean equals(Object o) {
        ComponentFamily other = (ComponentFamily)o;
        return other.compTypes.equals(compTypes);
    }

    @Override
    public int hashCode() {
        return compTypes.hashCode();
    }
}
