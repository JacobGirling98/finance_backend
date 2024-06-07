package acceptance.setup

import org.http4k.client.ApacheClient
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response

class ClientForTest(port: Int) {

    private val client = ApacheClient()
    private val baseUrl = "http://localhost:$port"

    fun put(url: String, body: String): Response = mutate(url, body, Method.PUT)

    fun post(url: String, body: String): Response = mutate(url, body, Method.POST)

    fun get(url: String, queries: Map<String, String> = emptyMap()): Response {
        val request = Request(Method.GET, "$baseUrl$url").let {
            queries.entries.fold(it) { req, query ->
                req.query(
                    query.key,
                    query.value
                )
            }
        }

        return client(request)
    }

    private fun mutate(url: String, body: String, method: Method): Response {
        val request = Request(method, "$baseUrl$url").body(body)
        return client(request)
    }
}
