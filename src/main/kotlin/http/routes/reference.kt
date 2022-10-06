package http.routes

import http.handlers.categoriesHandler
import org.http4k.core.Method
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import referenceData

val categoriesRoute: RoutingHttpHandler = "/categories" bind Method.GET to categoriesHandler(referenceData::categories)

val referenceRoutes = routes(
    "/reference" bind routes(
        categoriesRoute
    )
)

