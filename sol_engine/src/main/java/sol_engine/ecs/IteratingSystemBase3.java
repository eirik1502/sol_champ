package sol_engine.ecs;

import sol_engine.utils.Function;

public class IteratingSystemBase3 extends SystemBase {

    private Class<? extends Component> compType1, compType2, compType3;
    private Function.FourArg<Entity, Component, Component, Component> updateFunc;

    @SuppressWarnings("unchecked")
    protected <A extends Component, B extends Component, C extends Component> void updateWithComponents(
            Class<A> compType1,
            Class<B> compType2,
            Class<C> compType3,
            Function.FourArg<Entity, A, B, C> updateFunc) {
        this.compType1 = compType1;
        this.compType2 = compType2;
        this.compType3 = compType3;
        this.updateFunc = (Function.FourArg<Entity, Component, Component, Component>) updateFunc;
    }

    @Override
    protected final void onSetup() {
        usingComponents(compType1, compType2, compType3);
    }

    @Override
    protected final void onUpdate() {
        forEachWithComponents(
                compType1,
                compType2,
                compType3,
                (entity, comp1, comp2, comp3) -> updateFunc.invoke(entity, comp1, comp2, comp3)
        );
    }


}
