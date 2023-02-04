package http.handler

import domain.DateRange
import domain.Transaction
import http.lense.dateRangeListLens
import http.lense.transactionListLens
import http.param.extractDateRange
import org.http4k.core.HttpHandler
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with

fun dateRangeHandler(get: () -> List<DateRange>): HttpHandler = {
    Response(Status.OK).with(dateRangeListLens of get())
}

fun transactionsHandler(filter: (DateRange) -> List<Transaction>): HttpHandler = { request ->
    val transactions = filter(request.extractDateRange())
    Response(Status.OK).with(transactionListLens of transactions)
}