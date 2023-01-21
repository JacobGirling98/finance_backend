package http.handler

import domain.DateRange
import domain.Transaction
import http.lense.dateRangeListLens
import http.lense.endDateQueryLens
import http.lense.startDateQueryLens
import http.lense.transactionListLens
import org.http4k.core.HttpHandler
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with

fun dateRangeHandler(get: () -> List<DateRange>): HttpHandler = {
    Response(Status.OK).with(dateRangeListLens of get())
}

fun transactionsHandler(filter: (DateRange) -> List<Transaction>): HttpHandler = { request ->
    val startDate = startDateQueryLens.required("start").extract(request)
    val endDate = endDateQueryLens.required("end").extract(request)
    val transactions = filter(DateRange(startDate, endDate))
    Response(Status.OK).with(transactionListLens of transactions)
}