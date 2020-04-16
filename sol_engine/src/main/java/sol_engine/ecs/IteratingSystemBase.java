package sol_engine.ecs;


import sol_engine.utils.Function;

import java.util.List;
import java.util.Set;

public abstract class IteratingSystemBase extends SystemBase {

    private List<Class<? extends Component>> compTypes;
    private Function updateFunc;

    protected <T extends Component> void updateWithComponents(
            Class<T> compType,
            Function.TwoArg<Entity, T> updateFunc
    ) {
        compTypes = List.of(compType);
        this.updateFunc = updateFunc;
    }

    protected <A extends Component, B extends Component> void updateWithComponents(
            Class<A> compType1,
            Class<B> compType2,
            Function.ThreeArg<Entity, A, B> updateFunc
    ) {
        compTypes = List.of(compType1, compType2);
        this.updateFunc = updateFunc;
    }

    protected <A extends Component, B extends Component, C extends Component> void updateWithComponents(
            Class<A> compType1,
            Class<B> compType2,
            Class<C> compType3,
            Function.FourArg<Entity, A, B, C> updateFunc
    ) {
        compTypes = List.of(compType1, compType2, compType3);
        this.updateFunc = updateFunc;
    }

    protected <A extends Component, B extends Component, C extends Component, D extends Component>
    void updateWithComponents(
            Class<A> compType1,
            Class<B> compType2,
            Class<C> compType3,
            Class<D> compType4,
            Function.FiveArg<Entity, A, B, C, D> updateFunc
    ) {
        compTypes = List.of(compType1, compType2, compType3, compType4);
        this.updateFunc = updateFunc;
    }

    protected <A extends Component, B extends Component, C extends Component, D extends Component, E extends Component>
    void updateWithComponents(
            Class<A> compType1,
            Class<B> compType2,
            Class<C> compType3,
            Class<D> compType4,
            Class<E> compType5,
            Function.SixArg<Entity, A, B, C, D, E> updateFunc
    ) {
        compTypes = List.of(compType1, compType2, compType3, compType4, compType5);
        this.updateFunc = updateFunc;
    }

    protected abstract void onSetupWithUpdate();

    @Override
    protected final void onSetup() {
        onSetupWithUpdate();

        usingComponents(Set.copyOf(compTypes));
    }

    @Override
    @SuppressWarnings("unchecked")
    protected final void onUpdate() {
        switch (compTypes.size()) {
            case 1:
                forEachWithComponents(
                        compTypes.get(0),
                        (entity, comp) -> ((Function.TwoArg) updateFunc).invoke(entity, comp)
                );
                break;
            case 2:
                forEachWithComponents(
                        compTypes.get(0),
                        compTypes.get(1),
                        (entity, comp1, comp2) -> ((Function.ThreeArg) updateFunc).invoke(entity, comp1, comp2)
                );
                break;
            case 3:
                forEachWithComponents(
                        compTypes.get(0),
                        compTypes.get(1),
                        compTypes.get(2),
                        (entity, comp1, comp2, comp3) ->
                                ((Function.FourArg) updateFunc).invoke(entity, comp1, comp2, comp3)
                );
                break;
            case 4:
                forEachWithComponents(
                        compTypes.get(0),
                        compTypes.get(1),
                        compTypes.get(2),
                        compTypes.get(3),
                        (entity, comp1, comp2, comp3, comp4) ->
                                ((Function.FiveArg) updateFunc).invoke(entity, comp1, comp2, comp3, comp4)
                );
                break;
            case 5:
                forEachWithComponents(
                        compTypes.get(0),
                        compTypes.get(1),
                        compTypes.get(2),
                        compTypes.get(3),
                        compTypes.get(4),
                        (entity, comp1, comp2, comp3, comp4, comp5) ->
                                ((Function.SixArg) updateFunc).invoke(entity, comp1, comp2, comp3, comp4, comp5)
                );
                break;
        }

    }


}
