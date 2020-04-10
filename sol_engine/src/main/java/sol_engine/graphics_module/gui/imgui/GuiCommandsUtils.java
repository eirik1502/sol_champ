package sol_engine.graphics_module.gui.imgui;

import glm_.vec2.Vec2;
import imgui.MutableProperty0;
import org.joml.Vector2f;
import sol_engine.utils.Function;
import sol_engine.utils.mutable_primitives.MBoolean;
import sol_engine.utils.mutable_primitives.MFloat;
import sol_engine.utils.mutable_primitives.MInt;
import sol_engine.utils.mutable_primitives.MString;

import java.util.Arrays;

public class GuiCommandsUtils {

    public static final int INPUT_STRING_MAX_LENGTH = 100;

    private static final MutableProperty0<Integer> imguiMPropInt = new MutableProperty0<>();
    private static final MutableProperty0<Boolean> imguiMPropBoolean = new MutableProperty0<>();
    private static final boolean[] imguiMPropBooleanArray = new boolean[100]; // make it large due to internal imgui bug
    private static final MutableProperty0<Float> imguiMPropFloat = new MutableProperty0<>();
    //    private static final MutableProperty0<String> imguiMPropString = new MutableProperty0<>();
    private static final MutableProperty0<?> imguiMPropObject = new MutableProperty0<>();

    private static final Vec2 imguiVec2Zero = new Vec2();
    private static final Vec2 imguiVec2 = new Vec2();
    private static final Vec2 imguiVec2Two = new Vec2();
    private static final Vec2 imguiVec2Three = new Vec2();


    static boolean withConvertedMFloat(MFloat value, Function.OneArgReturn<MutableProperty0<Float>, Boolean> withConvertedValue) {
        imguiMPropFloat.set(value.value);
        boolean ret = withConvertedValue.invoke(imguiMPropFloat);
        value.value = imguiMPropFloat.get();
        return ret;
    }

    static boolean withConvertedMInt(MInt value, Function.OneArgReturn<MutableProperty0<Integer>, Boolean> withConvertedValue) {
        imguiMPropInt.set(value.value);
        boolean ret = withConvertedValue.invoke(imguiMPropInt);
        value.value = imguiMPropInt.get();
        return ret;
    }

    static boolean withConvertedMBoolean(MBoolean value, Function.OneArgReturn<MutableProperty0<Boolean>, Boolean> withConvertedValue) {
        imguiMPropBoolean.set(value.value);
        boolean ret = withConvertedValue.invoke(imguiMPropBoolean);
        value.value = imguiMPropBoolean.get();
        return ret;
    }

    static boolean withConvertedMBooleanArray(MBoolean value, Function.OneArgReturn<boolean[], Boolean> withConvertedValue) {
//        imguiMPropBooleanArray[0] = value.value;
        Arrays.fill(imguiMPropBooleanArray, value.value);
        boolean ret = withConvertedValue.invoke(imguiMPropBooleanArray);
        value.value = imguiMPropBooleanArray[0];
        return ret;
    }

    static boolean withConvertedMString(MString value, Function.OneArgReturn<byte[], Boolean> withConvertedValue) {
        byte[] valueCharsBytes = value.value.getBytes();
        byte[] imguiMPropBytes = new byte[INPUT_STRING_MAX_LENGTH];
        System.arraycopy(valueCharsBytes, 0, imguiMPropBytes, 0, valueCharsBytes.length);
        boolean ret = withConvertedValue.invoke(imguiMPropBytes);
//        value.value = Arrays.toString(imguiMPropString).stripTrailing();
        value.value = new String(imguiMPropBytes).stripTrailing();
        return ret;
    }

    static float withConvertedFloat(float value, Function.OneArgReturn<MutableProperty0<Float>, Boolean> withConvertedValue) {
        imguiMPropFloat.set(value);
        withConvertedValue.invoke(imguiMPropFloat);
        return imguiMPropFloat.get();
    }

    static int withConvertedInt(int value, Function.OneArgReturn<MutableProperty0<Integer>, Boolean> withConvertedValue) {
        imguiMPropInt.set(value);
        withConvertedValue.invoke(imguiMPropInt);
        return imguiMPropInt.get();
    }

    static boolean withConvertedBoolean(boolean value, Function.OneArgReturn<MutableProperty0<Boolean>, Boolean> withConvertedValue) {
        imguiMPropBoolean.set(value);
        withConvertedValue.invoke(imguiMPropBoolean);
        return imguiMPropBoolean.get();
    }

    static boolean withConvertedBooleanArray(boolean value, Function.OneArgReturn<boolean[], Boolean> withConvertedValue) {
//        imguiMPropBooleanArray[0] = value;
        Arrays.fill(imguiMPropBooleanArray, value);
        withConvertedValue.invoke(imguiMPropBooleanArray);
        return imguiMPropBooleanArray[0];
    }

    static String withConvertedMString(String value, Function.OneArgReturn<byte[], Boolean> withConvertedValue) {
        MString mstring = new MString(value);
        withConvertedMString(mstring, withConvertedValue);
        return mstring.value;
    }

    static MutableProperty0<Boolean> convertBoolean(boolean value) {
        imguiMPropBoolean.set(value);
        return imguiMPropBoolean;
    }

    static MutableProperty0<Float> convertFloat(float value) {
        imguiMPropFloat.set(value);
        return imguiMPropFloat;
    }

    static MutableProperty0<Integer> convertInt(int value) {
        imguiMPropInt.set(value);
        return imguiMPropInt;
    }

    private static Vec2 convertVector2(Vector2f source, Vec2 target) {
        target.setX(source.x);
        target.setY(source.y);
        return target;
    }

    static Vec2 convertVector2(Vector2f vec) {
        return convertVector2(vec, imguiVec2);
    }

    static Vec2 convertVector2Two(Vector2f vec) {
        return convertVector2(vec, imguiVec2Two);
    }

    static Vec2 convertVector2Three(Vector2f vec) {
        return convertVector2(vec, imguiVec2Three);
    }

    static Vec2 getImguiVector2Zero() {
        return imguiVec2Zero;
    }


    static int combineGuiFlags(GuiFlags... flags) {
        return Arrays.stream(flags)
                .map(GuiFlags::getI)
                .reduce(0, (acc, curr) -> acc | curr);
    }

}
