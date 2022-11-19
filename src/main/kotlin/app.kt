import config.logger
import config.properties
import dao.LoginDatabase
import dao.ReferenceData
import dao.StandingOrdersDatabase
import dao.TransactionsDatabase
import http.filter.lastLoginFilter
import http.filter.logResponseFilter
import http.git.GitClient
import http.route.gitRoutes
import http.route.loginRoutes
import http.route.referenceRoutes
import http.route.transactionRoutes
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.then
import org.http4k.filter.AllowAll
import org.http4k.filter.CorsPolicy
import org.http4k.filter.OriginPolicy
import org.http4k.filter.ServerFilters.Cors
import org.http4k.routing.routes
import org.http4k.server.SunHttp
import org.http4k.server.asServer
import resource.StandingOrderProcessor

val referenceData = ReferenceData(properties.dataLocation)
val transactionsDatabase = TransactionsDatabase(properties.dataLocation)
val loginDatabase = LoginDatabase(properties.dataLocation)
val standingOrderDatabase = StandingOrdersDatabase(properties.dataLocation)

val standingOrderProcessor = StandingOrderProcessor(standingOrderDatabase, transactionsDatabase)


val routes: HttpHandler = routes(
    referenceRoutes(referenceData),
    transactionRoutes(transactionsDatabase),
    loginRoutes(loginDatabase),
    gitRoutes(GitClient("/.."))
)

fun main() {
    referenceData.initialise()
    loginDatabase.initialise()

    standingOrderProcessor.schedule()

    val printingApp: HttpHandler = Cors(
        CorsPolicy(
            OriginPolicy.AllowAll(),
            listOf("Authorization", "Accept", "content-type"),
            Method.values().toList(),
            true
        )
    )
        .then(lastLoginFilter { loginDatabase.save(it) })
        .then(logResponseFilter())
        .then(routes)

    val server = printingApp.asServer(SunHttp(9000)).start()

    logger.info { "Server started on port ${server.port()}" }
}