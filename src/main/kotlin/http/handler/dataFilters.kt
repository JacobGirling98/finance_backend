package http.handler

import domain.DateRange
import domain.Transaction
import http.lense.dateRangeListLens
import http.lense.transactionListLens
import org.http4k.core.HttpHandler
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import org.http4k.lens.Query

fun dateRangeHandler(get: () -> List<DateRange>): HttpHandler = {
    Response(Status.OK).with(dateRangeListLens of get())
}

fun transactionsHandler(filter: (DateRange) -> List<Transaction>): HttpHandler = { request ->
    val startLens = Query.required("start")
    val endLens = Query.required("end")
    val start = startLens(request)
    Response(Status.OK)
}