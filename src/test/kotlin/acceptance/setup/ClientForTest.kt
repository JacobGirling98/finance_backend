package acceptance.setup

import org.http4k.client.ApacheClient
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response

class ClientForTest(port: Int) {

    private val client = ApacheClient()
    private val baseUrl = "http://localhost:$port"

    fun post(url: String, body: String): Response {
        val request = Request(Method.POST, "$baseUrl$url").body(body)
        return client(request)
    }

    fun get(url: String): Response {
        val request = Request(Method.GET, "$baseUrl$url")
        return client(request)
    }
}