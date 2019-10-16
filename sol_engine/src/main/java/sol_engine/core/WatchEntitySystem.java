package sol_engine.core;

import sol_engine.ecs.Component;
import sol_engine.ecs.Entity;
import sol_engine.graphics_module.GraphicsModule;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class WatchEntitySystem extends ModuleSystemBase {

    private static class WatchData {

    }

    private Map<Entity, WatchData> entityData = new HashMap<>();

    @Override
    protected void onSetup() {
        usingComponents(WatchComp.class);
        usingModules(GraphicsModule.class);
    }

    static int index = 0;

    @Override
    protected void onUpdate() {
        GraphicsModule graphics = getModule(GraphicsModule.class);

        entities.forEach(e -> {
            WatchData data = entityData.computeIfAbsent(e, newEntity -> new WatchData());
            Set<Class<? extends Component>> compTypes = e.getComponentTypeGroup().stream().collect(Collectors.toSet());

            compTypes.forEach(compType -> {

            });


            graphics.getRenderer().getImgui().draw(imgui -> {
                boolean[] pOpen = {true};
                imgui.beginMenuBar();
                imgui.beginMenu("Entity", true);

//                imgui.endMenu();
//                imgui.endMenuBar();

//                imgui.setNextWindowPos(new Vec2(0, 0), Cond.Once, new Vec2());

                compTypes.forEach(compType -> {
//                    imgui.begin("hei", pOpen, WindowFlag.AlwaysAutoResize.i);

//                    imgui.beginChildFrame(index++, new Vec2(500, 500), WindowFlag.AlwaysAutoResize.i);
//                    if (imgui.button(compType.getSimpleName(), new Vec2(128, 32))) {
//                        System.out.println(compType.getSimpleName());
//                    }
                    imgui.menuItem(compType.getSimpleName(), "i2", false, true);

                    Component c = e.getComponent(compType);
                    Arrays.stream(c.getClass().getDeclaredFields())
                            .forEach(field -> {
                                try {
                                    imgui.text(field.getName() + ": " + field.get(c));
                                } catch (IllegalAccessException ex) {
                                    ex.printStackTrace();
                                }
                            });

//                    imgui.endChildFrame();
                });
//                imgui.end();
            });
        });
        index = 0;
    }
}
