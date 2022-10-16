import config.DATA_LOC
import dao.CsvDatabase
import dao.ReferenceData
import http.routes.referenceRoutes
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.then
import org.http4k.filter.AllowAll
import org.http4k.filter.CorsPolicy
import org.http4k.filter.DebuggingFilters.PrintRequestAndResponse
import org.http4k.filter.OriginPolicy
import org.http4k.filter.ServerFilters.Cors
import org.http4k.routing.routes
import org.http4k.server.SunHttp
import org.http4k.server.asServer

val referenceData = ReferenceData(DATA_LOC)

val app: HttpHandler = routes(
    referenceRoutes(CsvDatabase(DATA_LOC))
)

fun main() {
    referenceData.initialise()

    val printingApp: HttpHandler = PrintRequestAndResponse()
        .then(Cors(CorsPolicy(OriginPolicy.AllowAll(), listOf("Authorization"), Method.values().toList(), true)))
        .then(app)

    val server = printingApp.asServer(SunHttp(9000)).start()

    println("Server started on port ${server.port()}")
}