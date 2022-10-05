package http.lense

import domain.Category
import org.http4k.core.Body
import org.http4k.lens.BiDiBodyLens
import http.lense.MyJackson.auto


val categoriesLens: BiDiBodyLens<List<Category>> = Body.auto<List<Category>>().toLens()