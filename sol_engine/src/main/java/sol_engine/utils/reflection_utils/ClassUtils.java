package sol_engine.utils.reflection_utils;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class ClassUtils {

    @SuppressWarnings("unchecked")
    public static <T> T instantiateNoargs(Class<T> clazz) {
        try {
            Constructor<?>[] constructors = clazz.getConstructors();
            boolean hasNoArgConstructor = Arrays.stream(constructors)
                    .anyMatch(constructor -> constructor.getParameterCount() == 0);
            if (hasNoArgConstructor) {
                return clazz.getConstructor().newInstance();
            }

            // handle potential vararg constructor (this is not registered as no-arg, in kotlin at least)
            Constructor<?> singleArrayArgConstructor = Arrays.stream(constructors)
                    .filter(constructor -> constructor.getParameterCount() == 1)
                    .filter(constructor -> constructor.getParameterTypes()[0].isArray())
                    .findFirst().orElse(null);
            if (singleArrayArgConstructor != null) {
                Class<?> arrayType = singleArrayArgConstructor.getParameterTypes()[0];
                Object emptyArray = Array.newInstance(arrayType.getComponentType(), 0);
                return (T) singleArrayArgConstructor.newInstance(emptyArray);
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        System.err.println("Failed to instanciate class: " + clazz.getSimpleName());
        return null;
    }

    public static <T> T instanciateNoargOf(String classPath, Class<T> of) {
        Class<? extends T> classOf = toClassOf(classPath, of);
        if (classOf == null) return null;
        return instantiateNoargs(classOf);
    }

    public static Object instantiateNoargs(String classPath) {
        Class<?> genericClass = toClass(classPath);
        if (genericClass == null) return null;
        return instantiateNoargs(genericClass);
    }

    public static Class<?> toClass(String classPath) {
        try {
            return Class.forName(classPath);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static <T> Class<? extends T> toClassOf(String classPath, Class<T> as) {
        try {
            return toClass(classPath).asSubclass(as);
        } catch (ClassCastException | NullPointerException e) {
            e.printStackTrace();
            return null;
        }
    }

}
