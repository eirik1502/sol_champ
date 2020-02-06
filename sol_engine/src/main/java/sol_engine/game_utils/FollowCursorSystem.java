package sol_engine.game_utils;

import sol_engine.core.ModuleSystemBase;
import sol_engine.core.TransformComp;
import sol_engine.graphics_module.GraphicsModule;
import sol_engine.input_module.InputGuiSourceModule;

public class FollowCursorSystem extends ModuleSystemBase {
    @Override
    public void onSetup() {
        usingComponents(FollowCursorComp.class, TransformComp.class);
        usingModules(InputGuiSourceModule.class, GraphicsModule.class);
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onUpdate() {
        InputGuiSourceModule inpMod = getModule(InputGuiSourceModule.class);

        forEachWithComponents(TransformComp.class,
                (entity, transComp) -> {
                    transComp.setPosition(inpMod.cursorPosition().x, inpMod.cursorPosition().y);
                }
        );
    }

    @Override
    public void onEnd() {

    }
}
