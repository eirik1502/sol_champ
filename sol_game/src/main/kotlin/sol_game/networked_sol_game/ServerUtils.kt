package sol_game.sol_game_server

import java.io.IOException
import java.net.ServerSocket
import java.net.URL


fun findFreeSocketPort(): Int {
    var socket: ServerSocket? = null
    try {
        socket = ServerSocket(0)
        socket.reuseAddress = true
        val port = socket.localPort
        try {
            socket.close()
        } catch (e: IOException) { // Ignore IOException on close()
        }
        return port
    } catch (e: IOException) {
    } finally {
        if (socket != null) {
            try {
                socket.close()
            } catch (e: IOException) {
            }
        }
    }
    return -1
}

class QueryParamsParser(
        url: String
) {

    val queryParamsMap: Map<String, String?>

    init {
        queryParamsMap = splitQuery(URL(url).query)
    }

    fun get(key: String): String? {
        return queryParamsMap[key]
    }

    fun has(key: String): Boolean {
        return queryParamsMap.containsKey(key)
    }

    fun hasAll(vararg keys: String): Boolean {
        return keys.all(this::has)
    }

    private fun splitQuery(queryParams: String): Map<String, String?> {
        return if (queryParams.isEmpty()) {
            emptyMap()
        } else queryParams.split("&")
                .map(this::splitQueryParameter)
                .toMap()
    }

    private fun splitQueryParameter(it: String): Pair<String, String?> {
        val idx = it.indexOf("=")
        val key = if (idx > 0) it.substring(0, idx) else it
        val value = if (idx > 0 && it.length > idx + 1) it.substring(idx + 1) else null
        return key to value
    }
}