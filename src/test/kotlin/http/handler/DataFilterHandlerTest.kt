package http.handler

import domain.DateRange
import domain.EndDate
import domain.StartDate
import io.kotest.core.spec.style.FunSpec

class DataFilterHandlerTest: FunSpec({

    test("can get date range from handler") {
        val dateRanges = { listOf(DateRange(StartDate(2020, 1, 1), EndDate(2021, 1, 1))) }
        val handler = dateRangeHandler(dateRanges)
    }

})