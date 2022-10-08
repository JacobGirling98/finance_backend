package http.routes

import http.handlers.referenceHandler
import org.http4k.core.Method.GET
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import referenceData

val categoriesRoute: RoutingHttpHandler = "/categories" bind GET to referenceHandler { referenceData.categories }
val accountsRoute: RoutingHttpHandler = "/accounts" bind GET to referenceHandler { referenceData.accounts }
val sourcesRoute: RoutingHttpHandler = "/sources" bind GET to referenceHandler { referenceData.sources }
val payeesRoute: RoutingHttpHandler = "/payees" bind GET to referenceHandler { referenceData.payees }

val referenceRoutes = routes(
    "/reference" bind routes(
        categoriesRoute,
        accountsRoute,
        sourcesRoute,
        payeesRoute
    )
)

