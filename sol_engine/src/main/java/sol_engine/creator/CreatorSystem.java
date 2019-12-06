package sol_engine.creator;

import glm_.vec4.Vec4;
import imgui.Col;
import sol_engine.core.ModuleSystemBase;
import sol_engine.ecs.Entity;
import sol_engine.graphics_module.GraphicsModule;
import sol_engine.utils.mutable_primitives.MBoolean;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class CreatorSystem extends ModuleSystemBase {
    private static class CreatorFrameEntry {
        CreatorFrame frame;
        MBoolean active;
        String name;

        private CreatorFrameEntry(CreatorFrame frame, boolean startActive, String name) {
            this.frame = frame;
            this.active = new MBoolean(startActive);
            this.name = name;
        }
    }

    //    private Map<Entity, WatchData> entityData = new HashMap<>();
    private Map<Entity, boolean[]> entitiesWatched = new HashMap<>();
    private final boolean[] tempBoolArr = {false};

    private Set<CreatorFrameEntry> creatorFramesEntries = new HashSet<>();

    public CreatorSystem() {
        creatorFramesEntries = Set.of(
                new CreatorFrameEntry(new WorldEditor(), false, "World-editor"),
                new CreatorFrameEntry(new WorldProfiler(), false, "World-profiler")
        );
    }

    @Override
    protected void onSetup() {
        usingComponents(EditorEditableComp.class);
        usingModules(GraphicsModule.class);
    }

    @Override
    protected void onStart() {
        creatorFramesEntries.forEach(entry -> entry.frame.onStart(world));
    }

    @Override
    protected void onEnd() {
        creatorFramesEntries.forEach(entry -> entry.frame.onEnd(world));
    }

    @Override
    protected void onUpdate() {
        GraphicsModule graphics = getModule(GraphicsModule.class);

        graphics.getRenderer().getGui().draw(gui -> {
            if (gui.menu.beginMainMenuBar()) {
                creatorFramesEntries.forEach(entry -> {
                    gui.getNative().pushStyleColor(Col.Text, entry.active.value
                            ? new Vec4(1, 1, 0.5, 1)
                            : new Vec4(1, 1, 1, 1)
                    );
                    gui.menu.menuItem(entry.name, "", entry.active, true);
                    gui.getNative().popStyleColor(1);
                });

                gui.menu.endMainMenuBar();
            }
            creatorFramesEntries.stream()
                    .filter(entry -> entry.active.value)
                    .forEach(entry -> entry.frame.draw(gui, world, graphics));
        });
    }

}

