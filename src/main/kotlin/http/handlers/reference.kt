package http.handlers

import domain.Category
import http.lense.categoriesLens
import org.http4k.core.*
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind

fun categoriesHandler(categoriesFunc: () -> List<Category>): RoutingHttpHandler = "/categories" bind Method.GET to {
    Response(Status.OK).with(categoriesLens of categoriesFunc())
}