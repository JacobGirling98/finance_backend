package http.routes

import dao.CsvDatabase
import http.handlers.descriptionsHandler
import http.handlers.postCreditTransactionHandler
import http.handlers.referenceHandler
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.routing.bind
import org.http4k.routing.routes
import referenceData

fun referenceRoutes(database: CsvDatabase) = routes(
    "/reference" bind routes(
        "/categories" bind GET to referenceHandler { referenceData.categories },
        "/accounts" bind GET to referenceHandler { referenceData.accounts },
        "/sources" bind GET to referenceHandler { referenceData.sources },
        "/payees" bind GET to referenceHandler { referenceData.payees },
        "/descriptions" bind GET to descriptionsHandler { referenceData.descriptions }
    ),
    "/transaction" bind routes(
        "/credit" bind POST to postCreditTransactionHandler { database.save(it) }
    )
)

