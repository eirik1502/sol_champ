package sol_engine.creator;

import glm_.vec2.Vec2;
import glm_.vec4.Vec4;
import imgui.Col;
import imgui.Cond;
import sol_engine.ecs.Component;
import sol_engine.ecs.Entity;
import sol_engine.ecs.SystemBase;
import sol_engine.ecs.World;
import sol_engine.graphics_module.GraphicsModule;
import sol_engine.graphics_module.gui.imgui.GuiCommands;
import sol_engine.graphics_module.gui.imgui.GuiWindowFlags;
import sol_engine.utils.math.MathF;

import java.util.*;
import java.util.stream.Collectors;

public class EntitiesFlowView implements CreatorFrame {

    private Map<Class<? extends SystemBase>, Set<Entity>> systemsEntities = Collections.emptyMap();

    private Map<Entity, Vec4> entitiesColors = new HashMap<>();

    private Vec4 randomColor() {
        float r = MathF.random();
        float g = MathF.randRange(1 - r, 1);
        float b = MathF.randRange((r + g) / 2f, 1);
        return new Vec4(r, g, b, 1);
    }

    @Override
    public boolean draw(GuiCommands gui, World world, GraphicsModule graphicsModule) {
        if (gui.begin("Entities-Flow", true,
                GuiWindowFlags.HorizontalScrollbar
        )) {
            if (gui.getNative().button("Record", new Vec2())) {
                systemsEntities = world.insight.getSystems().stream()
                        .collect(Collectors.toMap(
                                system -> system.getClass(),
                                system -> system.entitiesStream().collect(Collectors.toSet()),
                                (e1, e2) -> e1,
                                LinkedHashMap::new
                        ));
            }
            systemsEntities.forEach((systemType, entities) -> {
                String systemName = systemType.getSimpleName();

                gui.getNative().setNextItemOpen(true, Cond.Once);
                if (gui.container.collapsingHeader(systemName)) {
                    gui.core.text("\t");
                    entities.stream().forEach(entity -> {

                        Vec4 color = entitiesColors.computeIfAbsent(entity, (key) -> randomColor());

                        gui.format.sameLine();
                        gui.getNative().pushStyleColor(Col.Text, color);
                        gui.core.text(entity.name + " ");
                        gui.getNative().popStyleColor(1);
                    });
                }
            });
        }

        return true;
    }
}

