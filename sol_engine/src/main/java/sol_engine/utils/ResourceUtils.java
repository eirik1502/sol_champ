package sol_engine.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ResourceUtils {

    public static String loadResourceAsString(String file) {
        StringBuilder result = new StringBuilder();
        try {
            InputStream rs = ResourceUtils.class.getClassLoader().getResourceAsStream(file);

            BufferedReader reader = new BufferedReader(new InputStreamReader(rs));
            String buffer;
            while ((buffer = reader.readLine()) != null) {
                result.append(buffer + '\n');
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toString();
    }
}
