package http.lense

import domain.StartDate
import domain.EndDate
import org.http4k.lens.Query
import org.http4k.lens.localDate


val startDateQueryLens = Query.localDate().map(::StartDate) { it.value }
val endDateQueryLens = Query.localDate().map(::EndDate) { it.value }
