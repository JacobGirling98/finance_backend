package http.handler

import dao.Entity
import domain.DateRange
import domain.Transaction
import http.lense.dateRangeListLens
import http.lense.transactionEntityListLens
import http.param.extractDateRange
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Status


fun dateRangeHandler(get: () -> List<DateRange>) = handlerFor(
    { get() },
    Status.OK,
    { },
    dateRangeListLens
)

fun transactionsHandler(filterByDate: (DateRange) -> List<Entity<Transaction>>): HttpHandler = handlerFor(
    filterByDate,
    Status.OK,
    Request::extractDateRange,
    transactionEntityListLens
)