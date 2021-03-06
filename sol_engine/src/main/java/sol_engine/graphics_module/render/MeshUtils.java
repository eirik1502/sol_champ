package sol_engine.graphics_module.render;

import sol_engine.utils.collections.EqualTypedPair;
import sol_engine.utils.math.MathF;
import sol_engine.utils.stream.CollectorsUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class MeshUtils {
    public static Mesh createUnitCorneredRectangleMesh() {
        return new Mesh(
                new float[]{
                        0.0f, 0.0f, 0.0f,
                        0.0f, 1.0f, 0.0f,
                        1.0f, 1.0f, 0.0f,
                        1.0f, 0.0f, 0.0f
                },
                new byte[]{
                        0, 1, 2,
                        2, 3, 0
                }
        );
    }

    public static Mesh createUnitCenteredCircleMesh(int sides) {
        int verticesCount = (1 + sides);
        int verticesLength = verticesCount * 3;
        int indicesLength = sides * 3;
        final float anglesPerSide = 2 * MathF.PI / sides;

        List<Float> vertices = new ArrayList<>(verticesLength);
        List<Byte> indices = new ArrayList<>(indicesLength);

        // add the center vertex
        vertices.addAll(Arrays.asList(0f, 0f, 0f));

        IntStream.range(0, sides)
                .mapToObj(i -> anglesPerSide * i)
                .map(angle -> new EqualTypedPair<>(MathF.cos(angle) * 0.5f, MathF.sin(angle) * 0.5f))
                .flatMap(position -> Stream.of(position.getFirst(), position.getLast(), 0f))
                .forEach(vertices::add);

        IntStream.range(1, sides)
                .flatMap(i -> IntStream.of(0, i, i + 1))
                .forEach(index -> indices.add((byte) index));
        //add the last triangle
        IntStream.of(0, verticesCount - 1, 1).forEach(index -> indices.add((byte) index));

        return new Mesh(
                vertices.stream().collect(CollectorsUtils.toFloatArray()),
                indices.stream().collect(CollectorsUtils.toByteArray())
        );
    }

    public static Mesh createUnitCenteredCirclePointingMesh(int sides) {
        int verticesCount = (1 + sides);
        int verticesLength = verticesCount * 3;
        int indicesLength = sides * 3;
        final float anglesPerSide = 2 * MathF.PI / sides;

        float pointAngle = MathF.PI / 2;

        List<Float> vertices = new ArrayList<>(verticesLength);
        List<Byte> indices = new ArrayList<>(indicesLength);

        // add the center vertex
        vertices.addAll(Arrays.asList(0f, 0f, 0f));

        IntStream.range(0, MathF.floori(sides * 0.75f) + 1)
                .mapToObj(i -> anglesPerSide * i + pointAngle / 2)
                .map(angle -> new EqualTypedPair<>(MathF.cos(angle) * 0.5f, MathF.sin(angle) * 0.5f))
                .flatMap(position -> Stream.of(position.getFirst(), position.getLast(), 0f))
                .forEach(vertices::add);

        IntStream.range(0, MathF.floori(sides * 0.75f) + 1)
                .flatMap(i -> IntStream.of(0, i, i + 1))
                .forEach(index -> indices.add((byte) index));

        //add the pointer triangle
        vertices.addAll(List.of(0.7f, 0f, 0f));
        verticesCount = vertices.size() / 3;
        // point triangle
        indices.addAll(List.of((byte) 1, (byte) (verticesCount - 2), (byte) (verticesCount - 1)));
        // triangle between point and base
        indices.addAll(List.of((byte) 1, (byte) 0, (byte) (verticesCount - 2)));

        return new Mesh(
                vertices.stream().collect(CollectorsUtils.toFloatArray()),
                indices.stream().collect(CollectorsUtils.toByteArray())
        );
    }
}
