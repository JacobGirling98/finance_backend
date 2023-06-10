import config.environmentVariables
import config.logger
import config.mongoClient
import config.properties
import dao.LoginDatabase
import dao.ReferenceData
import dao.TransactionsDatabase
import dao.mongo.StandingOrderCollection
import http.filter.lastLoginFilter
import http.filter.logResponseFilter
import http.git.GitClient
import http.route.*
import org.http4k.contract.contract
import org.http4k.contract.openapi.ApiInfo
import org.http4k.contract.openapi.v3.OpenApi3
import org.http4k.contract.ui.swaggerUi
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.AllowAll
import org.http4k.filter.CorsPolicy
import org.http4k.filter.OriginPolicy
import org.http4k.filter.ServerFilters
import org.http4k.filter.ServerFilters.Cors
import org.http4k.routing.routes
import org.http4k.server.SunHttp
import org.http4k.server.asServer
import resource.StandingOrderProcessor
import java.time.LocalDate

val referenceData = ReferenceData(properties.dataLocation)
val transactionsDatabase = TransactionsDatabase(properties.dataLocation)
val loginDatabase = LoginDatabase(properties.dataLocation)
val standingOrderCollection = StandingOrderCollection(mongoClient)

val standingOrderProcessor = StandingOrderProcessor(standingOrderCollection, transactionsDatabase, LocalDate::now)

val contracts = listOf(
    referenceContracts(referenceData),
    transactionContracts(transactionsDatabase),
    loginContracts(loginDatabase),
    gitContracts(GitClient("${properties.dataLocation}/..", environmentVariables.githubToken)),
    dateRangeContracts { transactionsDatabase.data },
    headlineContracts { transactionsDatabase.data },
    standingOrdersContract(standingOrderCollection)
)

val swaggerUi = swaggerUi(
    Uri.of("spec"),
    title = "Finances API",
)

val api = contract {
    contracts.forEach { routes += it }
    renderer = OpenApi3(ApiInfo("Finances API", "1.0.0"))
    descriptionPath = "spec"
}

val routes: HttpHandler = routes(swaggerUi, api)

fun main() {
    referenceData.initialise()
    loginDatabase.initialise()
    transactionsDatabase.read()

    standingOrderProcessor.schedule()

    val printingApp: HttpHandler = Cors(
        CorsPolicy(
            OriginPolicy.AllowAll(),
            listOf("Authorization", "Accept", "content-type", "Access-Control-Allow-Origin"),
            Method.values().toList(),
            true
        )
    )
        .then(ServerFilters.CatchAll.invoke())
        .then(lastLoginFilter { loginDatabase.save(it) })
        .then(logResponseFilter())
        .then(routes)

    val server = printingApp.asServer(SunHttp(9000)).start()

    logger.info { "Server started on port ${server.port()}" }
}