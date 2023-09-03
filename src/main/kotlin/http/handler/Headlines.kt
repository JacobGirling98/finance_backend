package http.handler

import domain.Headlines
import domain.Transaction
import http.lense.headlinesLens
import http.param.extractDateRange
import org.http4k.core.HttpHandler
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import resource.incomeBetween
import resource.netIncomeBetween
import resource.savingsBetween
import resource.spendingBetween

fun headlinesHandler(data: () -> List<Transaction>): HttpHandler = { request ->
    val dates = request.extractDateRange()
    val headlines = data().let {
        Headlines(
            it.incomeBetween(dates),
            it.spendingBetween(dates),
            it.savingsBetween(dates),
            it.netIncomeBetween(dates)
        )
    }
    Response(Status.OK).with(headlinesLens of headlines)
}
