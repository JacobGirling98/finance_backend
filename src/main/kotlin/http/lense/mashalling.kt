package http.lense

import org.http4k.core.Body
import org.http4k.lens.BiDiBodyLens
import http.lense.MyJackson.auto


val referenceLens: BiDiBodyLens<List<String>> = Body.auto<List<String>>().toLens()