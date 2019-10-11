package sol_engine.ecs;

import sol_engine.utils.Function;

import java.util.stream.Stream;

public class EntitiesUtils {

    public static <T extends Component> void ForEachWithComponents(Stream<Entity> entitiesStream, Class<T> compType1, Function.TwoArg<Entity, T> consumer) {
        entitiesStream.forEach(e -> consumer.invoke(e, e.getComponent(compType1)));
    }

    public static <T extends Component, U extends Component> void ForEachWithComponents(
            Stream<Entity> entitiesStream,
            Class<T> compType1, Class<U> compType2, Function.ThreeArg<Entity, T, U> consumer) {
        entitiesStream.forEach(e -> consumer.invoke(e, e.getComponent(compType1), e.getComponent(compType2)));
    }

    public static <T extends Component, U extends Component, V extends Component> void ForEachWithComponents(
            Stream<Entity> entitiesStream,
            Class<T> compType1, Class<U> compType2, Class<V> compType3, Function.FourArg<Entity, T, U, V> consumer) {
        entitiesStream.forEach(e -> consumer.invoke(
                e,
                e.getComponent(compType1), e.getComponent(compType2), e.getComponent(compType3)));
    }

    public static <T extends Component, U extends Component, V extends Component, W extends Component> void ForEachWithComponents(
            Stream<Entity> entitiesStream,
            Class<T> compType1, Class<U> compType2, Class<V> compType3, Class<W> compType4,
            Function.FiveArg<Entity, T, U, V, W> consumer) {
        entitiesStream.forEach(e -> consumer.invoke(
                e,
                e.getComponent(compType1),
                e.getComponent(compType2), e.getComponent(compType3), e.getComponent(compType4)));
    }

    public static <T extends Component, U extends Component, V extends Component, W extends Component, X extends Component> void ForEachWithComponents(
            Stream<Entity> entitiesStream,
            Class<T> compType1, Class<U> compType2, Class<V> compType3, Class<W> compType4, Class<X> compType5,
            Function.SixArg<Entity, T, U, V, W, X> consumer) {
        entitiesStream.forEach(e -> consumer.invoke(
                e,
                e.getComponent(compType1),
                e.getComponent(compType2),
                e.getComponent(compType3),
                e.getComponent(compType4),
                e.getComponent(compType5))
        );
    }
}
