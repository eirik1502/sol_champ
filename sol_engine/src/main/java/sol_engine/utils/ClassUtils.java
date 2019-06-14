package sol_engine.utils;

import java.lang.reflect.InvocationTargetException;

public class ClassUtils {

//    public static <T extends B, B> T instanciateNoargOf(Class<T> clazz, Class<B> of) {
//        return instanciateNoarg(clazz);
//    }

    public static <T> T instanciateNoarg(Class<T> clazz) {
        try {
            return clazz.getConstructor().newInstance();
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
        return instanciateNoarg(classOf);
    }

    public static Object instanciateNoarg(String classPath) {
        Class<?> genericClass = toClass(classPath);
        if (genericClass == null) return null;
        return instanciateNoarg(genericClass);
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
