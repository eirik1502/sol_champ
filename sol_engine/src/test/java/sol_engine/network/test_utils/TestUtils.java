package sol_engine.network.test_utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TestUtils {
    private static final int shortTime = 50;

    public static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void sleepShort() {
        sleep(shortTime);
    }

    public static Object callPrivateMethod(Object object, String methodName, Object... argtypeArg) {
        Class<?>[] paramTypes = IntStream.range(0, argtypeArg.length)
                .filter(i -> i % 2 == 0)
                .mapToObj(i -> argtypeArg[i])
                .map(o -> (Class<?>) o).toArray(Class<?>[]::new);
        Object[] args = IntStream.range(0, argtypeArg.length)
                .filter(i -> i % 2 == 1)
                .mapToObj(i -> argtypeArg[i])
                .toArray();
        System.out.println("args " + Arrays.toString(args));
        Object ret = null;
        try {
            Method m = object.getClass().getDeclaredMethod(methodName, paramTypes);
            m.setAccessible(true);
            ret = m.invoke(object, args);

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return ret;
    }
}
