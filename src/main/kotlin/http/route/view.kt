package http.route

import domain.Transaction
import org.http4k.routing.bind
import org.http4k.routing.routes

fun viewRoutes(data: () -> List<Transaction>) = routes(
    "/view" bind routes(
        headlineRoutes(data)
    )
)