package http.handler

import domain.DateRange
import http.lense.dateRangeLens
import org.http4k.core.HttpHandler
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with

fun dateRangeHandler(get: () -> List<DateRange>): HttpHandler = {
    Response(Status.OK).with(dateRangeLens of get())
}
