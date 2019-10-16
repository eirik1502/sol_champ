package sol_engine.core;

import glm_.vec2.Vec2;
import imgui.Cond;
import imgui.WindowFlag;
import sol_engine.ecs.Component;
import sol_engine.ecs.Entity;
import sol_engine.graphics_module.GraphicsModule;
import sol_engine.utils.stream.WithIndex;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class WatchEntitySystem extends ModuleSystemBase {

    private static class WatchData {
        Map<Class<? extends Component>, boolean[]> selectedCompTypes = new HashMap<>();
    }

    private static final boolean[] FALSE_BOOL_ARR = {false};

    private Map<Entity, WatchData> entityData = new HashMap<>();

    @Override
    protected void onSetup() {
        usingComponents(WatchComp.class);
        usingModules(GraphicsModule.class);
    }

    static int index = 0;

    private Class<? extends Component> selectedComp = null;

    @Override
    protected void onUpdate() {
        GraphicsModule graphics = getModule(GraphicsModule.class);

        entities.forEach(e -> {
            final Set<Class<? extends Component>> compTypes = e.getComponentTypeGroup().stream().collect(Collectors.toSet());

            WatchData data = entityData.computeIfAbsent(e, newEntity -> {
                WatchData _data = new WatchData();
                compTypes.forEach(compType -> {
                    _data.selectedCompTypes.put(compType, FALSE_BOOL_ARR.clone());
                });
                return _data;
            });

            graphics.getRenderer().getImgui().draw(imgui -> {
                boolean[] pOpen = {true};

                imgui.setNextWindowPos(new Vec2(0, 0), Cond.Once, new Vec2());
                imgui.setNextWindowSize(new Vec2(256, 700), Cond.Once);
                if (imgui.begin("World", pOpen,
                        WindowFlag.NoMove.i |
                                WindowFlag.NoResize.i |
                                WindowFlag.NoCollapse.i |
                                WindowFlag.NoTitleBar.i |
                                WindowFlag.MenuBar.i |
                                WindowFlag.NoBringToFrontOnFocus.i
                )) {
                    if (imgui.beginMenuBar()) {
                        if (imgui.beginMenu("Entity", true)) {
                            compTypes.forEach(compType -> {
                                boolean[] menuItemSelected = data.selectedCompTypes.get(compType);
                                imgui.menuItem(compType.getSimpleName(), "", menuItemSelected, true);
//                                if (menuItemSelected[0]) {
//                                    selectedComp = compType;
//                                }
                            });
                            imgui.endMenu();
                        }
                        imgui.endMenuBar();

                        data.selectedCompTypes.entrySet().stream()
                                .filter(entry -> entry.getValue()[0])
                                .forEach(WithIndex.consumer((entry, index) -> {
                                    System.out.println(index);
                                    Class<? extends Component> compType = entry.getKey();
                                    boolean[] isCompTypeSelected = entry.getValue();

                                    if (imgui.begin(compType.getSimpleName(), isCompTypeSelected, WindowFlag.AlwaysAutoResize.i)) {

                                        Component c = e.getComponent(compType);
                                        Arrays.stream(c.getClass().getDeclaredFields())
                                                .forEach(field -> {
                                                    try {
                                                        imgui.text(field.getName() + ": " + field.get(c));
                                                    } catch (IllegalAccessException ex) {
                                                        ex.printStackTrace();
                                                    }
                                                });

                                        imgui.end();
                                    }
                                }));

                    }
                    imgui.end();
                }

            });
        });
        index = 0;
    }
}
