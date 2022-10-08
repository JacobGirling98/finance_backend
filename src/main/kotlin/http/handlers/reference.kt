package http.handlers

import domain.Description
import http.lense.descriptionsLens
import http.lense.referenceLens
import org.http4k.core.HttpHandler
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with

fun referenceHandler(referenceFunc: () -> List<String>): HttpHandler = {
    Response(Status.OK).with(referenceLens of referenceFunc())
}

fun descriptionsHandler(getDescriptions: () -> List<Description>): HttpHandler = {
    Response(Status.OK).with(descriptionsLens of getDescriptions())
}