package http.handler

import dao.Entity
import domain.DescriptionMapping
import http.lense.descriptionEntitiesLens
import http.lense.descriptionsLens
import http.lense.referenceEntitiesLens
import http.lense.stringLens
import org.http4k.core.HttpHandler
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import java.util.*

fun referenceHandler(referenceFunc: () -> List<Entity<String>>): HttpHandler = {
    Response(Status.OK).with(referenceEntitiesLens of referenceFunc())
}

fun descriptionsHandler(getDescriptions: () -> List<Entity<DescriptionMapping>>): HttpHandler = {
    Response(Status.OK).with(descriptionEntitiesLens of getDescriptions())
}

fun postDescriptionsHandler(save: (descriptions: List<DescriptionMapping>) -> List<UUID>): HttpHandler = { request ->
    save(descriptionsLens.extract(request))
    Response(Status.OK)
}

fun addTextTypeHandler(save: (String) -> UUID): HttpHandler = { request ->
    save(stringLens.extract(request))
    Response(Status.NO_CONTENT)
}
