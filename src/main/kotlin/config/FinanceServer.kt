package config

import config.contract.addAccountContact
import config.contract.addCategoryContact
import config.contract.addDescriptionsContract
import config.contract.addPayeesContact
import config.contract.addSourceContact
import config.contract.dateRangeContracts
import config.contract.getAccountsContract
import config.contract.getCategoriesContract
import config.contract.getDescriptionsContract
import config.contract.getPayeesContract
import config.contract.getSourcesContract
import config.contract.googleBackupContracts
import config.contract.headlineContracts
import config.contract.lastTransactionContracts
import config.contract.loginContracts
import config.contract.reminderContracts
import config.contract.standingOrdersContracts
import config.contract.transactionContracts
import http.filter.lastLoginFilter
import http.filter.logResponseFilter
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
import org.http4k.routing.routes
import org.http4k.server.Http4kServer
import org.http4k.server.SunHttp
import org.http4k.server.asServer

class FinanceServer(port: Int) {

    fun start(): Http4kServer {
        val server = server.start()
        logger.info { "Server started on port ${server.port()}" }
        return server
    }

    fun stop() {
        server.stop()
    }

    private val contracts = listOf(
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
        dateRangeContracts { transactionDatabase.selectAll() },
        headlineContracts { transactionDatabase.selectAll().map { it.domain } },
        standingOrdersContracts(standingOrderDatabase),
        lastTransactionContracts { transactionsProcessor.mostRecentUserTransaction() },
        googleBackupContracts(synchronisableDatabases, googleDriveSynchroniser),
        reminderContracts(reminderProcessor)
    )

    private val swaggerUi = swaggerUi(
        Uri.of("spec"),
        title = "Finances API"
    )

    private val api = contract {
        contracts.forEach { routes += it }
        renderer = OpenApi3(ApiInfo("Finances API", "1.0.0"))
        descriptionPath = "spec"
    }

    private val routes: HttpHandler = routes(swaggerUi, api)

    private val printingApp: HttpHandler = ServerFilters.Cors(
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

    private val server = printingApp.asServer(SunHttp(port))
}
