package http.contract

import domain.Headlines
import domain.Transaction
import domain.Value
import http.asTag
import http.handler.headlinesHandler
import http.lense.endDateQuery
import http.lense.headlinesLens
import http.lense.startDateQuery
import org.http4k.contract.meta
import org.http4k.core.Method.GET
import org.http4k.core.Status.Companion.OK

private const val URL = "/view/headlines"

fun headlineContracts(data: () -> List<Transaction>) = listOf(headlinesRoute(data))

private fun headlinesRoute(data: () -> List<Transaction>) = URL meta {
    operationId = URL
    summary = "Get headline stats for given date range"
    tags += URL.asTag()
    queries += startDateQuery
    queries += endDateQuery
    returning(
        OK,
        headlinesLens to Headlines(
            Value.of(0.0),
            Value.of(0.0),
            Value.of(0.0),
            Value.of(0.0)
        )
    )
} bindContract GET to headlinesHandler(data)
