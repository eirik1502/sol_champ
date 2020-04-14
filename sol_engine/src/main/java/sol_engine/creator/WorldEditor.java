package sol_engine.creator;

import glm_.vec2.Vec2;
import glm_.vec4.Vec4;
import imgui.Col;
import imgui.Cond;
import imgui.MutableProperty0;
import org.joml.Vector2f;
import sol_engine.ecs.Component;
import sol_engine.ecs.Entity;
import sol_engine.ecs.World;
import sol_engine.graphics_module.GraphicsModule;
import sol_engine.graphics_module.gui.imgui.*;
import sol_engine.utils.mutable_primitives.MBoolean;
import sol_engine.utils.stream.WithIndex;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class WorldEditor implements CreatorFrame {

    private float menuSizePercent = 0.2f;
    private List<Entity> entitiesWatching = new ArrayList<>();


    public boolean draw(GuiCommands imgui, World world, GraphicsModule graphicsModule) {
        Vector2f windowSize = graphicsModule.getWindow().getWindowSize();
        float leftMenuWidth = windowSize.x * menuSizePercent;
        float topMenuHeight = windowSize.y * menuSizePercent;

        List<Entity> entities = new ArrayList<>(world.insight.getEntities());
        entities.sort((e1, e2) -> e2.name.compareTo(e1.getName()));

        float mainMenuBarHeight = imgui.getNative().getTextLineHeightWithSpacing();
        imgui.format.setNextWindowPos(new Vector2f(0, mainMenuBarHeight), GuiCond.Once);
        imgui.format.setNextWindowSize(new Vector2f(leftMenuWidth, windowSize.y - mainMenuBarHeight), GuiCond.Once);
        if (imgui.begin("World", true,
//                    GuiWindowFlags.NoMove,
//                    GuiWindowFlags.NoResize,
//                    GuiWindowFlags.NoCollapse,
                GuiWindowFlags.NoTitleBar,
                GuiWindowFlags.MenuBar,
//                    GuiWindowFlags.NoBringToFrontOnFocus,
                GuiWindowFlags.HorizontalScrollbar
        )) {
            entities.forEach(entity -> {
                String className = entity.className == null ? "" : entity.className;
                boolean active = entitiesWatching.contains(entity);
                if (active) imgui.getNative().pushStyleColor(Col.Button, new Vec4(1, 1, 1, 0));
                if (imgui.getNative().button(entity.name + " (" + className + ")" + "##" + entity.hashCode(), new Vec2(0, 0))) {
                    if (!active) {
                        entitiesWatching.add(entity);
                    } else {
                        entitiesWatching.remove(entity);
                    }
                }
                if (active) imgui.getNative().popStyleColor(1);
            });
            new ArrayList<>(entitiesWatching).stream()
                    .forEach(entity -> {
                        String className = entity.className == null ? "" : entity.className;
                        MBoolean windowOpen = new MBoolean(true);
                        if (imgui.begin(entity.name + " (" + className + ")" + "##" + entity.hashCode(), windowOpen)) {
                            final Set<Class<? extends Component>> compTypes = entity.getComponentTypeGroup().stream().collect(Collectors.toSet());
                            compTypes.stream().forEach(compType -> drawComponent(imgui, entity, compType));

                            imgui.end();
                        }
                        if (!windowOpen.value) {
                            entitiesWatching.remove(entity);
                        }
                    });
            imgui.end();
        }
        return true;
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
        Field[] fields = obj.getClass().getDeclaredFields();
        if (fields.length == 0) {
            imgui.core.text(indentationStr(indentationCount) + "{ no data to show }");
        } else {
            Arrays.stream(fields)
                    .filter(field -> !Modifier.isStatic(field.getModifiers()))
                    .forEach(WithIndex.consumer((field, i) -> drawInputOfFieldWithValue(imgui, field, obj, indentationCount)));
        }

    }

    private void drawInputOfFieldWithValue(GuiCommands imgui, Field field, Object obj) {
        drawInputOfFieldWithValue(imgui, field, obj, 0);
    }

    private void drawInputOfFieldWithValue(GuiCommands imgui, Field field, Object obj, int indentationCount) {
        field.setAccessible(true);
        Class<?> fieldType = field.getType();
        String fieldName = field.getName();
        try {
            Object value = field.get(obj);
            boolean fieldIsFinal = Modifier.isFinal(field.getModifiers());
            boolean fieldIsPrimitive = fieldType.isPrimitive();
            boolean fieldDeactivated = fieldIsPrimitive && fieldIsFinal;

            drawInputOfValue(imgui, fieldName, value, "" + field.hashCode(), fieldDeactivated, indentationCount);

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void drawInputOfValue(GuiCommands imgui, String fieldName, Object value, String fieldLabel, boolean deactivated, int indentationCount) {
        String indentationStr = indentationStr(indentationCount);

        if (indentationCount > 7) {
            imgui.core.text(indentationStr + "{ max depth }");
            return;
        }

        imgui.core.text(indentationStr + fieldName + " ");
        imgui.format.sameLine();

        if (value == null) {
            imgui.core.text("null");
        } else {
            try {
                List<GuiItemStatusFlag> itemStatusFlags = new ArrayList<>();
                if (deactivated) {
                    itemStatusFlags.add(GuiItemStatusFlag.Deactivated);
                    imgui.format.pushStyleVar(GuiStyleVar.Alpha, 0.5f);
                }

                GuiItemStatusFlag[] itemStatusFlagsArr = itemStatusFlags.toArray(new GuiItemStatusFlag[0]);
                String label = "##" + fieldName + value.getClass().hashCode() + fieldLabel;
                Object newValue = null;

                if (value instanceof Float) {
                    newValue = imgui.input.inputFloat(label, (Float) value, 1f, 10f, "%.3f", itemStatusFlagsArr);
                } else if (value instanceof Integer) {
                    newValue = imgui.input.inputInt(label, (Integer) value, 1, 10, itemStatusFlagsArr);
                } else if (value instanceof Boolean) {
                    newValue = imgui.core.checkbox(label, (Boolean) value, itemStatusFlagsArr);
                } else if (value instanceof String) {
                    newValue = imgui.input.inputText(label, (String) value, itemStatusFlagsArr);
                } else if (value instanceof Number) {
                    newValue = imgui.input.inputFloat(label, ((Number) value).floatValue(), 1f, 10f, "%.3f", itemStatusFlagsArr);
//                } else if (fieldIsPrimitive) {
//                    imgui.core.text(value.toString() + label);
                } else if (value instanceof Enum<?>) {
                    imgui.input.inputText(label, ((Enum<?>) value).toString(), itemStatusFlagsArr);
                } else if (value instanceof Entity) {
                    imgui.input.inputText(label, ((Entity) value).getName(), itemStatusFlagsArr);
                } else if (value instanceof Class) {
                    imgui.input.inputText(label, ((Class<?>) value).getName(), itemStatusFlagsArr);
                } else if (value instanceof Collection) {
                    drawCollectionCollapsed(imgui, label, (Collection<?>) value, indentationCount);
                } else if (value instanceof Map) {
                    drawMapCollapsed(imgui, label, (Map<?, ?>) value, indentationCount);
//                } else if (fieldType.isArray()) {
//                    drawCollapsingFields(imgui, label, Arrays.asList((Object[]) value), false, indentationCount);
                } else {
                    boolean noCollapse = value.getClass().getDeclaredFields().length <= 3;
                    drawFieldsCollapsed(imgui, label, value, noCollapse, indentationCount);
                }

//            if (val != null) field.set(obj, val);
            } catch (Exception e) {
//                System.err.println("Somthing went wrong when drawing field: " + fieldName + " of type: " + value.getClass() + " value: " + value);
//                e.printStackTrace();

                imgui.core.text("{ could not read value }");
            } finally {
                if (deactivated) imgui.format.popStyleVar();
            }
        }
    }

    private String indentationStr(int indentationCount) {
        return new String(new char[indentationCount * 3]).replace('\0', ' ');
    }

    private void drawFieldsCollapsed(GuiCommands imgui, String label, Object value, boolean startOpen, int indentationCount) {
        if (startOpen) imgui.getNative().setNextItemOpen(true, Cond.Once);
        imgui.getNative().pushStyleColor(Col.Header, new Vec4(1, 1, 1, 0));
        boolean showChildren = imgui.container.collapsingHeader(label);
        imgui.getNative().popStyleColor(1);
        if (showChildren) {
            drawFieldsOf(imgui, value, indentationCount + 1);
        }
    }

    private void drawCollectionCollapsed(GuiCommands imgui, String label, Collection<?> values, int indentationCount) {
        if (values.size() == 1) imgui.getNative().setNextItemOpen(true, Cond.Once);
        imgui.getNative().pushStyleColor(Col.Header, new Vec4(1, 1, 1, 0));
        boolean showChildren = imgui.container.collapsingHeader("(" + values.size() + ")" + label);
        imgui.getNative().popStyleColor(1);
        if (showChildren) {
            values.forEach(WithIndex.consumer((val, i) ->
                    drawInputOfValue(imgui, "" + i, val, label + i, false, indentationCount + 1)
            ));
        }
    }

    private void drawMapCollapsed(GuiCommands imgui, String label, Map<?, ?> values, int indentationCount) {
        if (values.size() == 1) imgui.getNative().setNextItemOpen(true, Cond.Once);
        imgui.getNative().pushStyleColor(Col.Header, new Vec4(1, 1, 1, 0));
        boolean showChildren = imgui.container.collapsingHeader("(" + values.size() + ")" + label);
        imgui.getNative().popStyleColor(1);
        if (showChildren) {
            values.entrySet().forEach(WithIndex.consumer((entry, i) -> {
                drawInputOfValue(imgui, "key" + i, entry.getKey(), label, false, indentationCount + 1);
                drawInputOfValue(imgui, "value" + i, entry.getValue(), label, false, indentationCount + 2);
            }));
        }
    }
}
