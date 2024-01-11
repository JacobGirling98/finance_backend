package http.lense

import domain.EndDate
import domain.PageNumber
import domain.PageSize
import domain.StartDate
import domain.TransactionType
import org.http4k.lens.Query
import org.http4k.lens.enum
import org.http4k.lens.int
import org.http4k.lens.localDate
import org.http4k.lens.string
import org.http4k.lens.uuid

val startDateQuery = Query.localDate().map(::StartDate) { it.value }.required("start")
val endDateQuery = Query.localDate().map(::EndDate) { it.value }.required("end")

val pageNumberQuery = Query.int().map(::PageNumber) { it.value }.required("pageNumber")
val pageSizeQuery = Query.int().map(::PageSize) { it.value }.required("pageSize")

val searchTermQuery = Query.string().required("value")

val idQuery = Query.uuid().required("id")

val optionalTransactionTypeQuery = Query.enum<TransactionType>().optional("type")
val optionalStartDateQuery = Query.localDate().map(::StartDate) { it.value }.optional("start")
val optionalEndDateQuery = Query.localDate().map(::EndDate) { it.value }.optional("end")
