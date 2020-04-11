package sol_engine.creator;

import glm_.vec4.Vec4;
import imgui.Col;
import imgui.Cond;
import imgui.StyleVar;
import sol_engine.ecs.Component;
import sol_engine.ecs.World;
import sol_engine.graphics_module.GraphicsModule;
import sol_engine.graphics_module.gui.imgui.GuiCommands;
import sol_engine.graphics_module.gui.imgui.GuiWindowFlags;
import sol_engine.utils.math.MathF;

import java.util.HashMap;
import java.util.Map;

public class SystemsView implements CreatorFrame {

    private Map<Class<? extends Component>, Vec4> compTypeColor = new HashMap<>();

    private Vec4 randomColor() {
        float r = MathF.random();
        float g = MathF.randRange(1 - r, 1);
        float b = MathF.randRange((r + g) / 2f, 1);
        return new Vec4(r, g, b, 1);
    }

    @Override
    public boolean draw(GuiCommands gui, World world, GraphicsModule graphicsModule) {
        if (gui.begin("Systems", true,
                GuiWindowFlags.HorizontalScrollbar
        )) {
            world.insight.getSystems().forEach(system -> {
                String systemName = system.getClass().getSimpleName();

                gui.getNative().setNextItemOpen(true, Cond.Once);
                if (gui.container.collapsingHeader(systemName)) {
                    gui.core.text("\t");
                    system.getCompFamily().stream().forEach(compType -> {
                        String compTypeName = compType.getSimpleName();
                        Vec4 color = compTypeColor.computeIfAbsent(compType, (key) -> randomColor());

                        gui.format.sameLine();
                        gui.getNative().pushStyleColor(Col.Text, color);
                        gui.core.text(compTypeName + " ");
                        gui.getNative().popStyleColor(1);
                    });
                }

//                if (gui.container.collapsingHeader(system.getClass().getSimpleName())) {
//                    system.getCompFamily().stream().forEach(compType -> {
//                        gui.core.text(compType.getSimpleName());
//                    });
//                }
            });
        }

        return true;
    }
}
