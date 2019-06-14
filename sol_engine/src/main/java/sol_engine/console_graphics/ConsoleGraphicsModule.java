package sol_engine.console_graphics;

import com.google.common.base.Joiner;

import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ConsoleGraphicsModule {

    public class ConsoleAvatar {
        public int x, y;
        public char avatar;
    }

    private int mapWidth = 20;
    private int mapHeight = 10;
    private char emptyChar = '.';

    private PrintWriter outputWriter = new PrintWriter(System.out, true);

    public ConsoleGraphicsModule() {

    }

    public void drawAvatars(List<ConsoleAvatar> avatars) {
        final List<List<Character>> emptyMapCopy = createEmptyMap(mapWidth, mapHeight);
        avatars.forEach(a -> emptyMapCopy.get(a.y).set(a.x, a.avatar));
    }

    private void drawConsoleMap(List<List<Character>> consoleMap) {
        consoleMap.forEach(mapRow -> System.out.println(Joiner.on("").join(mapRow)));
    }

    private List<List<Character>> createEmptyMap(int width, int height) {
        // create a map of empty characters to the size of the map
        return IntStream.range(0, height)
                .mapToObj(y -> IntStream.range(0, width)
                        .mapToObj(x -> emptyChar).collect(Collectors.toList()))
                .collect(Collectors.toList());
    }
}
