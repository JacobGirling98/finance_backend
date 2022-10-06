package http.routes

import http.handlers.referenceHandler
import org.http4k.core.Method
import org.http4k.core.Method.GET
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import referenceData

val categoriesRoute: RoutingHttpHandler = "/categories" bind GET to referenceHandler(referenceData::categories)
val accountsRoute: RoutingHttpHandler = "/accounts" bind GET to referenceHandler(referenceData::accounts)

val referenceRoutes = routes(
    "/reference" bind routes(
        categoriesRoute,
        accountsRoute
    )
)

