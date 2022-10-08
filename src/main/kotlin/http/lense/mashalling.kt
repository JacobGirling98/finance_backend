package http.lense

import domain.Description
import org.http4k.core.Body
import org.http4k.lens.BiDiBodyLens
import http.MyJackson.auto


val referenceLens: BiDiBodyLens<List<String>> = Body.auto<List<String>>().toLens()

val descriptionsLens: BiDiBodyLens<List<Description>> = Body.auto<List<Description>>().toLens()