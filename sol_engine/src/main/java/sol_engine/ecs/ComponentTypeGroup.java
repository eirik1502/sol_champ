package sol_engine.ecs;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class ComponentTypeGroup {

    private Set<Class<? extends Component>> compTypes;

    @SafeVarargs
    public ComponentTypeGroup(Class<? extends Component>... compTypes) {
        this(new HashSet<>(Arrays.asList(compTypes)));
    }
    public ComponentTypeGroup(Set<Class<? extends Component>> compTypes) {
        this.compTypes = new HashSet<>(compTypes);
    }

    public Stream<Class<? extends Component>> stream() {
        return compTypes.stream();
    }

    public boolean contains(ComponentTypeGroup other) {
        return compTypes.containsAll(other.compTypes);
    }

    @Override
    public boolean equals(Object o) {
        ComponentTypeGroup other = (ComponentTypeGroup)o;
        return other.compTypes.equals(compTypes);
    }

    @Override
    public int hashCode() {
        return compTypes.hashCode();
    }
}
