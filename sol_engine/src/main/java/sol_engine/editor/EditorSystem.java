package sol_engine.editor;

import glm_.vec2.Vec2;
import imgui.*;
import imgui.internal.ItemStatusFlag;
import org.joml.Vector2f;
import sol_engine.core.ModuleSystemBase;
import sol_engine.ecs.Component;
import sol_engine.ecs.Entity;
import sol_engine.graphics_module.GraphicsModule;
import sol_engine.utils.Function;
import sol_engine.utils.stream.WithIndex;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class EditorSystem extends ModuleSystemBase {

    private static class WatchData {
        Map<Class<? extends Component>, boolean[]> selectedCompTypes = new HashMap<>();
    }

    private static final boolean[] FALSE_BOOL_ARR = {false};
    private static final boolean[] TRUE_BOOL_ARR = {true};
    private static final Vec2 ZERO_VEC = new Vec2();

    private float menuSizePercent = 0.2f;

    //    private Map<Entity, WatchData> entityData = new HashMap<>();
    private Map<Entity, boolean[]> entitiesWatched = new HashMap<>();
    private final boolean[] tempBoolArr = {false};

    @Override
    protected void onSetup() {
        usingComponents(EditorEditableComp.class);
        usingModules(GraphicsModule.class);
    }

    @Override
    protected void onUpdate() {
        GraphicsModule graphics = getModule(GraphicsModule.class);

        Vector2f windowSize = graphics.getWindow().getWindowSize();
        float leftMenuWidth = windowSize.x * menuSizePercent;
        float topMenuHeight = windowSize.y * menuSizePercent;

        graphics.getRenderer().getImgui().draw(imgui -> {
            imgui.setNextWindowPos(new Vec2(0, 0), Cond.Always, new Vec2());
            imgui.setNextWindowSize(new Vec2(leftMenuWidth, windowSize.y), Cond.Once);
            if (imgui.begin("World", TRUE_BOOL_ARR,
                    WindowFlag.NoMove.i |
                            WindowFlag.NoResize.i |
                            WindowFlag.NoCollapse.i |
                            WindowFlag.NoTitleBar.i |
                            WindowFlag.MenuBar.i |
                            WindowFlag.NoBringToFrontOnFocus.i |
                            WindowFlag.HorizontalScrollbar.i
            )) {
                if (imgui.beginMenuBar()) {
                    if (imgui.beginMenu("entities", true)) {

                        entities.forEach(entity -> {
                            EditorEditableComp editComp = entity.getComponent(EditorEditableComp.class);

                            editComp.currentlyWatched = asMutable(editComp.currentlyWatched, mval ->
                                    imgui.menuItem(entity.name, "", mval, true));
                        });

                        imgui.endMenu();
                    }
                    imgui.endMenuBar();
                }
                entities.stream()
                        .filter(entity -> entity.getComponent(EditorEditableComp.class).currentlyWatched)
                        .forEach(entity -> {
                            imgui.text(entity.name);

                            final Set<Class<? extends Component>> compTypes = entity.getComponentTypeGroup().stream().collect(Collectors.toSet());
                            compTypes.stream().forEach(compType -> drawComponent(imgui, entity, compType));
                        });
                imgui.end();
            }
        });
    }

    private void drawComponent(ImGui imgui, Entity entity, Class<? extends Component> compType) {
        if (imgui.collapsingHeader(compType.getSimpleName() + "##" + entity.hashCode(), 0)) {
            Component comp = entity.getComponent(compType);
            drawFieldsOf(imgui, comp);
        }
    }

    private void drawFieldsOf(ImGui imgui, Object obj) {
        drawFieldsOf(imgui, obj, 0);
    }

    private void drawFieldsOf(ImGui imgui, Object obj, int indentationCount) {
        Field[] fields = obj.getClass().getFields();
        if (fields.length == 0) {
            imgui.text("{ no data }");
        } else {
            Arrays.stream(fields)
                    .filter(field -> !Modifier.isStatic(field.getModifiers()))
                    .forEach(WithIndex.consumer((field, i) -> drawInputOfField(imgui, field, obj, indentationCount)));
        }

    }

    @SuppressWarnings("unchecked")
    private Map<Type, Function.FourArg<ImGui, String, MutableProperty0<?>, Integer>> mapTypeToImguiInput = Map.of(
            float.class, (imgui, label, mval, itemFlags) -> imgui.inputFloat(label, (MutableProperty0<Float>) mval, 1f, 10f, "%.3f", itemFlags),
            int.class, (imgui, label, mval, itemFlags) -> imgui.inputInt(label, (MutableProperty0<Integer>) mval, 1, 10, itemFlags),
            boolean.class, (imgui, label, mval, itemFlags) -> {
                MutableProperty0<Boolean> mvalBool = ((MutableProperty0<Boolean>) mval);
                boolean[] mvalBoolArr = {mvalBool.get()};
                imgui.checkbox(label, mvalBoolArr, itemFlags);
                mvalBool.set(mvalBoolArr[0]);
            },
            String.class, (imgui, label, mval, itemFlags) -> {
                MutableProperty0<String> stringMval = (MutableProperty0<String>) mval;
                char[] inputBuf = Arrays.copyOf(stringMval.get().toCharArray(), 100);
                imgui.inputText(label, inputBuf, 0, null, null);
                stringMval.set(new String(inputBuf).trim());
            },
            Vector2f.class, (imgui, label, mval, itemFlags) -> {
                MutableProperty0<Vector2f> vecMVal = (MutableProperty0<Vector2f>) mval;
                Vec2 imguiVec2 = new Vec2(vecMVal.get().x, vecMVal.get().y);
                imgui.inputVec2(label, imguiVec2, "%.3f", 0);
                vecMVal.get().set(imguiVec2.getX(), imguiVec2.getY());
            }
    );

    private void drawInputOfField(ImGui imgui, Field field, Object obj) {
        drawInputOfField(imgui, field, obj, 0);
    }

    private void drawInputOfField(ImGui imgui, Field field, Object obj, int indentationCount) {
        Class<?> fieldType = field.getType();
        try {
            var drawFunc = mapTypeToImguiInput.get(fieldType);
            String fieldName = field.getName();
            MutableProperty0<?> mval = new MutableProperty0<>(field.get(obj));
            boolean fieldIsFinal = Modifier.isFinal(field.getModifiers());

            int itemFlag = 0;
            if (fieldIsFinal) {
                itemFlag |= ItemStatusFlag.Deactivated.i;
                imgui.pushStyleVar(StyleVar.Alpha, 0.5f);
            }
            String initialIndentSpaces = new String(new char[indentationCount * 3]).replace('\0', ' ');
            imgui.text(initialIndentSpaces + fieldName + " ");
            imgui.sameLine();
            if (drawFunc != null) {
                drawFunc.invoke(imgui, "##" + fieldName + obj.hashCode(), mval, itemFlag);
                if (!fieldIsFinal) {
                    field.set(obj, mval.get());
                }
            } else if (!fieldType.isPrimitive()) {
                Object val = field.get(obj);
                if (imgui.collapsingHeader(val.getClass().getSimpleName() + "##" + obj.hashCode(), 0)) {
                    drawFieldsOf(imgui, val, indentationCount + 1);
                }
            } else {
                imgui.labelText(fieldName, mval.get().toString());
            }
            if (fieldIsFinal) {
                imgui.popStyleVar(1);
            }

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private <T> T asMutable(T value, Consumer<MutableProperty0<T>> withMutable) {
        MutableProperty0<T> mval = new MutableProperty0<>(value);
        withMutable.accept(mval);
        return mval.get();
    }
}

