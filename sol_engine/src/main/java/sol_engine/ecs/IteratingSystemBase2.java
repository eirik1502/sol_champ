package sol_engine.ecs;

import sol_engine.utils.Function;

public class IteratingSystemBase2 extends SystemBase {

    private Class<? extends Component> compType1, compType2;
    private Function.ThreeArg<Entity, Component, Component> updateFunc;

    @SuppressWarnings("unchecked")
    protected <A extends Component, B extends Component> void updateWithComponents(
            Class<A> compType1,
            Class<B> compType2,
            Function.ThreeArg<Entity, A, B> updateFunc) {
        this.compType1 = compType1;
        this.compType2 = compType2;
        this.updateFunc = (Function.ThreeArg<Entity, Component, Component>) updateFunc;
    }

    @Override
    protected final void onSetup() {
        usingComponents(compType1, compType2);
    }

    @Override
    protected final void onUpdate() {
        forEachWithComponents(
                compType1,
                compType2,
                (entity, comp1, comp2) -> updateFunc.invoke(entity, comp1, comp2)
        );
    }


}
