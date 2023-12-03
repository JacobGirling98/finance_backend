package http.lense

import domain.EndDate
import domain.PageNumber
import domain.PageSize
import domain.StartDate
import org.http4k.lens.Query
import org.http4k.lens.int
import org.http4k.lens.localDate

val startDateQuery = Query.localDate().map(::StartDate) { it.value }.required("start")
val endDateQuery = Query.localDate().map(::EndDate) { it.value }.required("end")

val pageNumberQuery = Query.int().map(::PageNumber) { it.value }.required("pageNumber")
val pageSizeQuery = Query.int().map(::PageSize) { it.value }.required("pageSize")
