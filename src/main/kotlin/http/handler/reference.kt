package http.handler

import domain.DescriptionMapping
import http.lense.descriptionsLens
import http.lense.referenceLens
import org.http4k.core.HttpHandler
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with

fun referenceHandler(referenceFunc: () -> List<String>): HttpHandler = {
    Response(Status.OK).with(referenceLens of referenceFunc())
}

fun descriptionsHandler(getDescriptions: () -> List<DescriptionMapping>): HttpHandler = {
    Response(Status.OK).with(descriptionsLens of getDescriptions())
}

fun postDescriptionsHandler(save: (descriptions: List<DescriptionMapping>) -> Unit): HttpHandler = { request ->
    save(descriptionsLens.extract(request))
    Response(Status.OK)
}