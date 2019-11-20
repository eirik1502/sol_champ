package sol_engine.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionsUtils {

    public static String exceptionStackTraceToString(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}
