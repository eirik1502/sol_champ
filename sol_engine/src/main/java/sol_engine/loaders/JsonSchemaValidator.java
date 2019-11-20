package sol_engine.loaders;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import sol_engine.utils.ExceptionsUtils;

public class JsonSchemaValidator {
    public static boolean validateJson(String schemaPath, String jsonPath) {
        JsonNode jsonNode = JsonLoader.loadJson(jsonPath);
        if (jsonNode == null) {
            LoadersLogger.logger.severe("Json path incorrect. For json path: " + schemaPath);
            return false;
        }
        return validateJson(schemaPath, jsonNode);
    }

    public static boolean validateJson(String schemaPath, JsonNode jsonNode) {
        JsonNode schemaNode = JsonLoader.loadJson(schemaPath);
        if (schemaNode == null) {
            LoadersLogger.logger.severe("Json schema path incorrect. For schema path: " + schemaPath);
            return false;
        }
        return validateJson(schemaNode, jsonNode);
    }

    public static boolean validateJson(JsonNode schemaNode, JsonNode jsonNode) {
        JsonSchema schema;
        try {
            JsonSchemaFactory schemaFactory = JsonSchemaFactory.byDefault();
            schema = schemaFactory.getJsonSchema(schemaNode);

        } catch (ProcessingException e) {
            LoadersLogger.logger.severe("Invalid json schema syntax. Details following:\n" +
                    ExceptionsUtils.exceptionStackTraceToString(e));
            return false;
        }

        try {
            ProcessingReport schemaReport = schema.validate(jsonNode);

            if (!schemaReport.isSuccess()) {
                LoadersLogger.logger.severe("json invalid given schema. Details following:\n"
                        + schemaReport);
                return false;
            }
            return true;

        } catch (ProcessingException e) {
            LoadersLogger.logger.severe("some other schema error than syntax? Details following:\n" +
                    ExceptionsUtils.exceptionStackTraceToString(e));
            return false;
        }
    }
}
