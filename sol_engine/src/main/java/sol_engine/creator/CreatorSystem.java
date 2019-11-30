package sol_engine.creator;

import glm_.vec2.Vec2;
import glm_.vec4.Vec4;
import imgui.Col;
import imgui.Cond;
import imgui.MutableProperty0;
import org.joml.Vector2f;
import sol_engine.core.ModuleSystemBase;
import sol_engine.ecs.Component;
import sol_engine.ecs.Entity;
import sol_engine.graphics_module.GraphicsModule;
import sol_engine.graphics_module.imgui.*;
import sol_engine.utils.stream.WithIndex;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class CreatorSystem extends ModuleSystemBase {

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

        graphics.getRenderer().getGui().draw(imgui -> {
            imgui.format.setNextWindowPos(new Vector2f(0, 0), GuiCond.Once);
            imgui.format.setNextWindowSize(new Vector2f(leftMenuWidth, windowSize.y), GuiCond.Once);
            if (imgui.begin("World", true,
//                    GuiWindowFlags.NoMove,
//                    GuiWindowFlags.NoResize,
//                    GuiWindowFlags.NoCollapse,
                    GuiWindowFlags.NoTitleBar,
                    GuiWindowFlags.MenuBar,
//                    GuiWindowFlags.NoBringToFrontOnFocus,
                    GuiWindowFlags.HorizontalScrollbar
            )) {
                if (imgui.menu.beginMenuBar()) {
                    if (imgui.menu.beginMenu("entities", true)) {

                        entities.forEach(entity -> {
                            EditorEditableComp editComp = entity.getComponent(EditorEditableComp.class);
                            editComp.currentlyWatched =
                                    imgui.menu.menuItem(entity.name, "", editComp.currentlyWatched, true);
                        });

                        imgui.menu.endMenu();
                    }
                    imgui.menu.endMenuBar();
                }
                entities.stream()
                        .filter(entity -> entity.getComponent(EditorEditableComp.class).currentlyWatched)
                        .forEach(entity -> {
                            imgui.core.text(entity.name);

                            final Set<Class<? extends Component>> compTypes = entity.getComponentTypeGroup().stream().collect(Collectors.toSet());
                            compTypes.stream().forEach(compType -> drawComponent(imgui, entity, compType));
                        });
                imgui.end();
            }
        });
    }

    private void drawComponent(GuiCommands imgui, Entity entity, Class<? extends Component> compType) {
        if (imgui.container.collapsingHeader(compType.getSimpleName() + "##" + entity.hashCode())) {
            Component comp = entity.getComponent(compType);
            drawFieldsOf(imgui, comp);
        }
    }

    private void drawFieldsOf(GuiCommands imgui, Object obj) {
        drawFieldsOf(imgui, obj, 0);
    }

    private void drawFieldsOf(GuiCommands imgui, Object obj, int indentationCount) {
        Field[] fields = obj.getClass().getFields();
        if (fields.length == 0) {
            imgui.core.text("{ no data }");
        } else {
            Arrays.stream(fields)
                    .filter(field -> !Modifier.isStatic(field.getModifiers()))
                    .forEach(WithIndex.consumer((field, i) -> drawInputOfField(imgui, field, obj, indentationCount)));
        }

    }

    @SuppressWarnings("unchecked")
//    private Map<Type, Function.FourArg<GuiCommands, String, MVal, Integer>> mapTypeToImguiInput = Map.of(
//            float.class, (imgui, label, mval, itemFlags) -> imgui.input.inputFloat(label, (MVal<Float>) mval, 1f, 10f, "%.3f", itemFlags),
//            int.class, (imgui, label, mval, itemFlags) -> imgui.inputInt(label, (MutableProperty0<Integer>) mval, 1, 10, itemFlags),
//            boolean.class, (imgui, label, mval, itemFlags) -> {
//                MutableProperty0<Boolean> mvalBool = ((MutableProperty0<Boolean>) mval);
//                boolean[] mvalBoolArr = {mvalBool.get()};
//                imgui.core.checkbox(label, mvalBoolArr, itemFlags);
//                mvalBool.set(mvalBoolArr[0]);
//            },
//            String.class, (imgui, label, mval, itemFlags) -> {
//                MutableProperty0<String> stringMval = (MutableProperty0<String>) mval;
//                char[] inputBuf = Arrays.copyOf(stringMval.get().toCharArray(), 100);
//                imgui.inputText(label, inputBuf, 0, null, null);
//                stringMval.set(new String(inputBuf).trim());
//            },
//            Vector2f.class, (imgui, label, mval, itemFlags) -> {
//                MutableProperty0<Vector2f> vecMVal = (MutableProperty0<Vector2f>) mval;
//                Vec2 imguiVec2 = new Vec2(vecMVal.get().x, vecMVal.get().y);
//                imgui.inputVec2(label, imguiVec2, "%.3f", 0);
//                vecMVal.get().set(imguiVec2.getX(), imguiVec2.getY());
//            }
//    );

    private void drawInputOfField(GuiCommands imgui, Field field, Object obj) {
        drawInputOfField(imgui, field, obj, 0);
    }

    private void drawInputOfField(GuiCommands imgui, Field field, Object obj, int indentationCount) {
        Class<?> fieldType = field.getType();
        String fieldName = field.getName();
        boolean fieldIsFinal = Modifier.isFinal(field.getModifiers());
        boolean fieldIsPrimitive = fieldType.isPrimitive();
        boolean fieldDeactivated = fieldIsPrimitive && fieldIsFinal;

        String indentationStr = new String(new char[indentationCount * 3]).replace('\0', ' ');
        System.out.println(field.getName() + " " + field.getType());
        try {
            imgui.core.text(indentationStr + fieldName + " ");
            imgui.format.sameLine();


            List<GuiItemStatusFlag> itemStatusFlags = new ArrayList<>();
            if (fieldDeactivated) {
                itemStatusFlags.add(GuiItemStatusFlag.Deactivated);
                imgui.format.pushStyleVar(GuiStyleVar.Alpha, 0.5f);
            }

            GuiItemStatusFlag[] itemStatusFlagsArr = itemStatusFlags.toArray(new GuiItemStatusFlag[0]);
            String label = "##" + fieldName + obj.hashCode();
            Object val = null;

            if (fieldType.equals(float.class)) {
                val = imgui.input.inputFloat(label, field.getFloat(obj), 1f, 10f, "%.3f", itemStatusFlagsArr);
            } else if (fieldType.equals(int.class)) {
                val = imgui.input.inputInt(label, field.getInt(obj), 1, 10, itemStatusFlagsArr);
            } else if (fieldType.equals(boolean.class)) {
                val = imgui.core.checkbox(label, field.getBoolean(obj), itemStatusFlagsArr);
            } else if (fieldType.equals(String.class)) {
                val = imgui.input.inputText(label, (String) field.get(obj), itemStatusFlagsArr);
            } else if (fieldIsPrimitive) {
                imgui.core.text(field.get(obj).toString() + label);
            } else {
                boolean noCollapse = field.get(obj).getClass().getFields().length <= 3;
                if (noCollapse) imgui.getNative().setNextItemOpen(true, Cond.Once);
                imgui.getNative().pushStyleColor(Col.Header, new Vec4(1, 1, 1, 0));
                boolean showChild = imgui.container.collapsingHeader(label);
                imgui.getNative().popStyleColor(1);
                if (showChild) {
                    drawFieldsOf(imgui, field.get(obj), indentationCount + 1);
                }
            }

//            if (val != null) field.set(obj, val);

            if (fieldDeactivated) imgui.format.popStyleVar();
        } catch (
                IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    private <T> T asMutable(T value, Consumer<MutableProperty0<T>> withMutable) {
        MutableProperty0<T> mval = new MutableProperty0<>(value);
        withMutable.accept(mval);
        return mval.get();
    }
}

