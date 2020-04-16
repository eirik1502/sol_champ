package sol_engine.ecs;


import sol_engine.utils.Function;

public class IteratingSystemBase1 extends SystemBase {

    private Class<? extends Component> compType;
    private Function.TwoArg<Entity, Component> updateFunc;

    @SuppressWarnings("unchecked")
    protected <T extends Component> void updateWithComponents(Class<T> compType, Function.TwoArg<Entity, T> updateFunc) {
        this.compType = compType;
        this.updateFunc = (Function.TwoArg<Entity, Component>) updateFunc;
    }

    @Override
    protected final void onSetup() {
        usingComponents(compType);
    }

    @Override
    protected final void onUpdate() {
        forEachWithComponents(compType,
                (entity, comp) -> updateFunc.invoke(entity, comp)
        );
    }


}
