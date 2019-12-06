package sol_engine.utils.collections;

import sol_engine.utils.stream.CollectorsUtils;

import java.util.List;

public class ArrayUtils {

    public static float[] listToFloatArray(List<Float> list) {
        return list.stream().collect(CollectorsUtils.toFloatArray());
    }

    public static byte[] listToByteArray(List<Byte> list) {
        return list.stream().collect(CollectorsUtils.toByteArray());
    }
}
