package http.handler

import config.CustomJackson
import domain.DateRange
import domain.EndDate
import http.lense.StartDate
import io.kotest.core.spec.style.FunSpec
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.kotest.shouldHaveBody

class DataFilterHandlerTest: FunSpec({

    test("can get date range from handler") {
        val dateRanges = { listOf(DateRange(StartDate.of(2020, 1, 1,), EndDate(2021, 1, 1))) }
        val handler = dateRangeHandler(dateRanges)

        handler(Request(Method.GET, "/")) shouldHaveBody dateRanges().asJson()
    }

})

private fun List<DateRange>.asJson(): String =
    CustomJackson.mapper.writeValueAsString(this)