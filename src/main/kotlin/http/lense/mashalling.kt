package http.lense

import domain.DescriptionMapping
import org.http4k.core.Body
import org.http4k.lens.BiDiBodyLens
import config.CustomJackson.auto


val referenceLens: BiDiBodyLens<List<String>> = Body.auto<List<String>>().toLens()

val descriptionsLens: BiDiBodyLens<List<DescriptionMapping>> = Body.auto<List<DescriptionMapping>>().toLens()