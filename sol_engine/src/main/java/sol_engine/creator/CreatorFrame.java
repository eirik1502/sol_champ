package sol_engine.creator;

import sol_engine.ecs.World;
import sol_engine.graphics_module.GraphicsModule;
import sol_engine.graphics_module.imgui.GuiCommands;

public interface CreatorFrame {

    default void onStart(World world) {
    }

    default void onEnd(World world) {
    }

    boolean draw(GuiCommands gui, World world, GraphicsModule graphicsModule);

}
