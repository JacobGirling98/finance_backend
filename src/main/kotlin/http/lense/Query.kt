package http.lense

import domain.EndDate
import domain.StartDate
import org.http4k.lens.Query
import org.http4k.lens.localDate

val startDateQuery = Query.localDate().map(::StartDate) { it.value }.required("start")
val endDateQuery = Query.localDate().map(::EndDate) { it.value }.required("end")
