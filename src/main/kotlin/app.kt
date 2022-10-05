import config.DATA_LOC
import dao.ReferenceData
import http.handlers.categoriesHandler
import org.http4k.core.*
import org.http4k.filter.DebuggingFilters.PrintRequestAndResponse
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.SunHttp
import org.http4k.server.asServer

val referenceData = ReferenceData(DATA_LOC)

val app: HttpHandler = routes(
    "/ping" bind Method.GET to {
        Response(Status.OK).body("pong")
    },
    categoriesHandler(referenceData::categories)
)

fun main() {
    referenceData.initialise()

    val printingApp: HttpHandler = PrintRequestAndResponse().then(app)

    val server = printingApp.asServer(SunHttp(9000)).start()

    println("Server started on port ${server.port()}")
}