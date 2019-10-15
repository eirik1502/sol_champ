package sol_engine.core;

import sol_engine.ecs.EcsLogger;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CoreLogger {
    public static final Logger logger = Logger.getLogger("core");

    private static List<Logger> projectLoggers = Arrays.asList(
            CoreLogger.logger,
            EcsLogger.logger
    );

    public static void disableAllLogging() {
        projectLoggers.forEach(logger -> logger.setLevel(Level.OFF));
    }

    public static void setAllLogLevel(Level level) {
        projectLoggers.forEach(logger -> logger.setLevel(level));
    }
}
