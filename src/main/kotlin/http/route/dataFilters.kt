package http.route

import domain.Transaction
import http.handler.dateRangeHandler
import http.handler.transactionsHandler
import org.http4k.core.Method.GET
import org.http4k.routing.bind
import org.http4k.routing.routes
import resource.*
import kotlin.collections.filter

fun dataFilterRoutes(data: () -> List<Transaction>) = routes(
    "/reference/months" bind GET to dateRangeHandler(monthsOf(data)),
    "/reference/years" bind GET to dateRangeHandler(yearsOf(data)),
    "/reference/fiscal-months" bind GET to dateRangeHandler(fiscalMonthsOf(data)),
    "/reference/fiscal-years" bind GET to dateRangeHandler(fiscalYearsOf(data))
)

fun dataRetrievalRoutes(data: () -> List<Transaction>) = routes(
    "/transactions" bind GET to transactionsHandler { data().filter(it) }
)