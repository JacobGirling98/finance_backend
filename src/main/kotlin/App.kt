
import config.environmentVariables
import config.logger
import config.properties
import dao.csv.*
import http.contract.*
import http.filter.lastLoginFilter
import http.filter.logResponseFilter
import http.git.GitClient
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
import resource.LoginSynchroniser
import resource.StandingOrderProcessor
import java.time.LocalDate
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

val accountDatabase = StringCsvDatabase(1.hours, "${properties.dataLocation}/accounts.csv")
val categoryDatabase = StringCsvDatabase(1.hours, "${properties.dataLocation}/categories.csv")
val incomeSourceDatabase = StringCsvDatabase(1.hours, "${properties.dataLocation}/income_sources.csv")
val payeeDatabase = StringCsvDatabase(1.hours, "${properties.dataLocation}/payees.csv")

val descriptionMappingDatabase =
    DescriptionMappingCsvDatabase(10.minutes, "${properties.dataLocation}/description_mappings.csv")

val transactionDatabase = TransactionCsvDatabase(10.seconds, "${properties.dataLocation}/transactions.csv")

val standingOrderDatabase = StandingOrderCsvDatabase(10.seconds, "${properties.dataLocation}/standing_orders.csv")

val loginDatabase = LoginCsvDatabase(1.hours, "${properties.dataLocation}/logins.csv")
val loginSynchroniser = LoginSynchroniser(loginDatabase)

val standingOrderProcessor = StandingOrderProcessor(standingOrderDatabase, transactionDatabase, LocalDate::now)


val contracts = listOf(
    listOf(
        categoriesContract { categoryDatabase.selectAll() },
        accountsContract { accountDatabase.selectAll() },
        sourcesContract { incomeSourceDatabase.selectAll() },
        payeesContract { payeeDatabase.selectAll() },
        getDescriptionsContract { descriptionMappingDatabase.selectAll() },
        addDescriptionsContract { descriptionMappingDatabase.save(it) }
    ),
    transactionContracts(transactionDatabase),
    loginContracts { loginDatabase.lastLogin() },
    gitContracts(GitClient("${properties.dataLocation}/..", environmentVariables.githubToken)),
    dateRangeContracts { transactionDatabase.selectAll() },
    headlineContracts { transactionDatabase.selectAll().map { it.domain } },
    standingOrdersContract(standingOrderDatabase)
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
        .then(lastLoginFilter { loginSynchroniser.addLogin(it) })
        .then(logResponseFilter())
        .then(routes)

    val server = printingApp.asServer(SunHttp(9000)).start()

    logger.info { "Server started on port ${server.port()}" }
}