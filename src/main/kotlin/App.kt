
import config.AppMode
import config.environmentVariables
import config.logger
import config.properties
import dao.csv.DescriptionMappingCsvDatabase
import dao.csv.LoginCsvDatabase
import dao.csv.StandingOrderCsvDatabase
import dao.csv.StringCsvDatabase
import dao.csv.TransactionCsvDatabase
import http.contract.addAccountContact
import http.contract.addCategoryContact
import http.contract.addDescriptionsContract
import http.contract.addPayeesContact
import http.contract.addSourceContact
import http.contract.dateRangeContracts
import http.contract.getAccountsContract
import http.contract.getCategoriesContract
import http.contract.getDescriptionsContract
import http.contract.getPayeesContract
import http.contract.getSourcesContract
import http.contract.gitContracts
import http.contract.googleBackupContracts
import http.contract.headlineContracts
import http.contract.lastTransactionContracts
import http.contract.loginContracts
import http.contract.standingOrdersContracts
import http.contract.transactionContracts
import http.filter.lastLoginFilter
import http.filter.logResponseFilter
import http.git.GitClient
import http.google.GoogleDrive
import http.google.Synchronisable
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
import resource.*
import java.time.LocalDate
import kotlin.time.Duration.Companion.milliseconds

val accountDatabase =
    StringCsvDatabase(properties.csv.account.sync.milliseconds, "${properties.dataLocation}/accounts.csv")
val categoryDatabase =
    StringCsvDatabase(properties.csv.category.sync.milliseconds, "${properties.dataLocation}/categories.csv")
val incomeSourceDatabase =
    StringCsvDatabase(properties.csv.incomeSource.sync.milliseconds, "${properties.dataLocation}/income_sources.csv")
val payeeDatabase = StringCsvDatabase(properties.csv.payee.sync.milliseconds, "${properties.dataLocation}/payees.csv")

val descriptionMappingDatabase =
    DescriptionMappingCsvDatabase(
        properties.csv.descriptionMapping.sync.milliseconds,
        "${properties.dataLocation}/description_mappings.csv"
    )

val transactionDatabase =
    TransactionCsvDatabase(properties.csv.transaction.sync.milliseconds, "${properties.dataLocation}/transactions.csv")
val transactionsProcessor = TransactionProcessor(transactionDatabase)

val standingOrderDatabase = StandingOrderCsvDatabase(
    properties.csv.standingOrder.sync.milliseconds,
    "${properties.dataLocation}/standing_orders.csv"
)

val loginDatabase = LoginCsvDatabase(properties.csv.login.sync.milliseconds, "${properties.dataLocation}/logins.csv")
val loginSynchroniser = LoginSynchroniser(loginDatabase)

val standingOrderProcessor = StandingOrderProcessor(standingOrderDatabase, transactionDatabase, LocalDate::now)

val googleDrive = GoogleDrive(properties.google.credentialsFile)
val googleDriveSynchroniser = GoogleDriveSynchroniser(googleDrive)
val synchronisableDatabases: List<Synchronisable> = listOf(
    descriptionMappingDatabase,
    transactionDatabase,
    standingOrderDatabase,
    loginDatabase,
    accountDatabase,
    categoryDatabase,
    payeeDatabase,
    incomeSourceDatabase
)

val contracts = listOf(
    listOf(
        getCategoriesContract { categoryDatabase.selectAll() },
        getAccountsContract { accountDatabase.selectAll() },
        getSourcesContract { incomeSourceDatabase.selectAll() },
        getPayeesContract { payeeDatabase.selectAll() },
        getDescriptionsContract { descriptionMappingDatabase.selectAll() },
        addDescriptionsContract { descriptionMappingDatabase.save(it) },
        addCategoryContact { categoryDatabase.save(it) },
        addAccountContact { accountDatabase.save(it) },
        addSourceContact { incomeSourceDatabase.save(it) },
        addPayeesContact { payeeDatabase.save(it) }
    ),
    transactionContracts(transactionDatabase, transactionsProcessor),
    loginContracts { loginDatabase.lastLogin() },
    gitContracts(GitClient("${properties.dataLocation}/..", environmentVariables.githubToken)),
    dateRangeContracts { transactionDatabase.selectAll() },
    headlineContracts { transactionDatabase.selectAll().map { it.domain } },
    standingOrdersContracts(standingOrderDatabase),
    lastTransactionContracts { transactionDatabase.selectAll().map { it.domain }.mostRecent() },
    googleBackupContracts(synchronisableDatabases, googleDriveSynchroniser)
)

val swaggerUi = swaggerUi(
    Uri.of("spec"),
    title = "Finances API"
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
    if (properties.appMode == AppMode.DEV) {
        logger.info { "Serving Swagger at http://localhost:9000" }
    }
}
