package http.handlers

import domain.Category
import http.lense.categoriesLens
import org.http4k.core.HttpHandler
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with

fun categoriesHandler(categoriesFunc: () -> List<Category>): HttpHandler = {
    Response(Status.OK).with(categoriesLens of categoriesFunc())
}

