package sol_engine.ecs;

public class AddTextSystem extends SystemBase {
    @Override
    protected void onSetup() {
        usingComponents(AddTextComp.class, TextComp.class);
    }

    @Override
    protected void onUpdate() {
        forEachWithComponents(
                AddTextComp.class,
                TextComp.class,
                (entity, addTextComp, textComp) -> {
                    textComp.text += addTextComp.textToAdd;
                }
        );
    }
}
