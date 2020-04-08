package sol_engine.network.network_utils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class NetworkUtils {

    public static String uuid() {
        return UUID.randomUUID().toString();
    }

    public static int findFreeSocketPort() {
        ServerSocket socket = null;
        try {
            socket = new ServerSocket(0);
            socket.setReuseAddress(true);
            int port = socket.getLocalPort();
            try {
                socket.close();
            } catch (IOException e) { // Ignore IOException on close()
            }
            return port;
        } catch (IOException e) {
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }
        return -1;
    }

    public static String toURLQuery(Map<String, String> params) {
        return "?" + params.entrySet()
                .stream()
                .map(entry -> entry.getKey() + "=" + (entry.getValue() != null ? entry.getValue() : ""))
                .collect(Collectors.joining("&"));
    }

    public static URI websocketsURI(String address, int port, Map<String, String> queryParams) {
        try {
            String uriString = String.format("ws://%s:%d", address, port) + toURLQuery(queryParams);
            return new URI(uriString);
        } catch (URISyntaxException e) {
            return null;
        }
    }

    public static Map<String, String> parseQueryParams(String url) {
        int queryStartIndex = url.lastIndexOf('?');
        if (queryStartIndex == -1 || queryStartIndex + 1 >= url.length()) {
            return new HashMap<>();
        }

        String queryStr = url.substring(queryStartIndex + 1);
        String[] queryStrElems = queryStr.split(Pattern.quote("&"));
        return Arrays.stream(queryStrElems)
                .map(elem -> elem.split(Pattern.quote("=")))
                .collect(Collectors.toMap(
                        keyVal -> keyVal[0],
                        keyVal -> keyVal.length >= 2 ? keyVal[1] : ""
                ));
    }
}
