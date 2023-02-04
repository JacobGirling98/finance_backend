package http.param

import domain.DateRange
import http.lense.endDateQueryLens
import http.lense.startDateQueryLens
import org.http4k.core.Request

fun Request.extractDateRange(): DateRange {
    val startDate = startDateQueryLens.required("start").extract(this)
    val endDate = endDateQueryLens.required("end").extract(this)
    return DateRange(startDate, endDate)
}