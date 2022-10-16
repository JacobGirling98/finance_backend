package http.routes

import http.handlers.descriptionsHandler
import http.handlers.referenceHandler
import org.http4k.core.Method.GET
import org.http4k.routing.bind
import org.http4k.routing.routes
import referenceData

val referenceRoutes = routes(
    "/reference" bind routes(
        "/categories" bind GET to referenceHandler { referenceData.categories },
        "/accounts" bind GET to referenceHandler { referenceData.accounts },
        "/sources" bind GET to referenceHandler { referenceData.sources },
        "/payees" bind GET to referenceHandler { referenceData.payees },
        "/descriptions" bind GET to descriptionsHandler { referenceData.descriptions }
    )
)

