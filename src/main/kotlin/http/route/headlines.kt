package http.route

import domain.Transaction
import http.handler.headlinesHandler
import org.http4k.core.Method
import org.http4k.routing.bind
import org.http4k.routing.routes

fun headlineRoutes(data: () -> List<Transaction>) = routes(
    "/headlines" bind Method.GET to headlinesHandler(data)
)