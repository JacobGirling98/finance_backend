package http.handler

import domain.DateRange
import domain.Transaction
import http.lense.dateRangeListLens
import http.lense.transactionEntityListLens
import http.param.extractDateRange
import dao.Entity
import org.http4k.core.HttpHandler
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with

fun dateRangeHandler(get: () -> List<DateRange>): HttpHandler = {
    Response(Status.OK).with(dateRangeListLens of get())
}

fun transactionsHandler(filterByDate: (DateRange) -> List<Entity<Transaction>>): HttpHandler = { request ->
    val transactions = filterByDate(request.extractDateRange())
    Response(Status.OK).with(transactionEntityListLens of transactions)
}