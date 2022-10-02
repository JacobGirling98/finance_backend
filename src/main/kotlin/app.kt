import org.http4k.core.*
import org.http4k.filter.DebuggingFilters.PrintRequest
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.SunHttp
import org.http4k.server.asServer

val app: HttpHandler = routes(
    "/ping" bind Method.GET to {
        Response(Status.OK).body("pong")
    },
    "/categories" bind Method.GET to {
        Response(Status.OK).body("")
    }
)

fun main() {
    val printingApp: HttpHandler = PrintRequest().then(app)

    val server = printingApp.asServer(SunHttp(9000)).start()

    println("Server started on port ${server.port()}")
}