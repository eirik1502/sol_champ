package sol_engine.loaders;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class JsonUtils {

    public static List<String> getArrayChildNodesAsText(JsonNode node) {
        List<String> list = new ArrayList<>();
        for (JsonNode subNode : node) {
            list.add(subNode.asText());
        }
        return list;
    }

    public static Stream<String> streamArrayChildNodesAsText(JsonNode parentNode) {
        return getArrayChildNodesAsText(parentNode).stream();
    }

    public static List<JsonNode> getArrayChildNodes(JsonNode parentNode) {
        List<JsonNode> childNodes = new ArrayList<>();
        for (JsonNode childNode : parentNode) {
            childNodes.add(childNode);
        }
        return childNodes;
    }

    public static Stream<JsonNode> streamArrayChildNodes(JsonNode parentArrayNode) {
        return getArrayChildNodes(parentArrayNode).stream();
    }
}
