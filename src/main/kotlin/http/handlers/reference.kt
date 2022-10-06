package http.handlers

import http.lense.referenceLens
import org.http4k.core.HttpHandler
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with

fun referenceHandler(referenceFunc: () -> List<String>): HttpHandler = {
    Response(Status.OK).with(referenceLens of referenceFunc())
}
