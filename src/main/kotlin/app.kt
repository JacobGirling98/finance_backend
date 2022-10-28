import config.DATA_LOC
import dao.LoginDatabase
import dao.ReferenceData
import dao.TransactionsDatabase
import http.filter.lastLoginFilter
import http.route.referenceRoutes
import http.route.transactionRoutes
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
val transactionsDatabase = TransactionsDatabase(DATA_LOC)
val loginDatabase = LoginDatabase(DATA_LOC)

val app: HttpHandler = routes(
    referenceRoutes(referenceData),
    transactionRoutes(transactionsDatabase)
)

fun main() {
    referenceData.initialise()
    loginDatabase.initialise()

    val printingApp: HttpHandler = PrintRequestAndResponse()
        .then(
            Cors(
                CorsPolicy(
                    OriginPolicy.AllowAll(),
                    listOf("Authorization", "Accept", "content-type"),
                    Method.values().toList(),
                    true
                )
            )
        )
        .then(lastLoginFilter { loginDatabase.save(it) })
        .then(app)

    val server = printingApp.asServer(SunHttp(9000)).start()

    println("Server started on port ${server.port()}")
}