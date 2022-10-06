import config.DATA_LOC
import dao.ReferenceData
import http.routes.referenceRoutes
import org.http4k.core.HttpHandler
import org.http4k.core.then
import org.http4k.filter.DebuggingFilters.PrintRequestAndResponse
import org.http4k.routing.routes
import org.http4k.server.SunHttp
import org.http4k.server.asServer

val referenceData = ReferenceData(DATA_LOC)

val app: HttpHandler = routes(
    referenceRoutes
)

fun main() {
    referenceData.initialise()

    val printingApp: HttpHandler = PrintRequestAndResponse().then(app)

    val server = printingApp.asServer(SunHttp(9000)).start()

    println("Server started on port ${server.port()}")
}