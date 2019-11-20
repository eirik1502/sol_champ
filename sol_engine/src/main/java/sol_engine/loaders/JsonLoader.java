package sol_engine.loaders;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

public class JsonLoader {

    private static ObjectMapper jsonMapper = new ObjectMapper();

    public static JsonNode loadJson(String jsonPath) {
        // load the config file
        InputStream configInputStream = WorldLoaderOld.class.getClassLoader().getResourceAsStream(jsonPath);

        // check if the config file exist
        if (configInputStream == null) {
            LoadersLogger.logger.severe("invalid path for: " + jsonPath);
            return null;
        }

        try {
            return jsonMapper.readTree(configInputStream);
        } catch (IOException e) {
            LoadersLogger.logger.severe("invalid json syntax in: " + jsonPath);
            return null;
        }
    }
}
