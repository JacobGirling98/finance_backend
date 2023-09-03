package http.param

import domain.DateRange
import http.lense.endDateQuery
import http.lense.startDateQuery
import org.http4k.core.Request

fun Request.extractDateRange() = DateRange(startDateQuery.extract(this), endDateQuery.extract(this))
