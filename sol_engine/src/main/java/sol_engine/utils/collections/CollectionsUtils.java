package sol_engine.utils.collections;

import sol_engine.utils.stream.CollectorsUtils;

import java.util.List;

public class CollectionsUtils {
    public static float[] toFloatArray(List<Float> list) {
        return list.stream().collect(CollectorsUtils.toFloatArray());
    }

    public static byte[] toByteArray(List<Byte> list) {
        return list.stream().collect(CollectorsUtils.toByteArray());
    }

}
