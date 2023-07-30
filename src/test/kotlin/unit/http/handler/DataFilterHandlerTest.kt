package unit.http.handler

import config.CustomJackson
import domain.DateRange
import domain.EndDate
import domain.StartDate
import domain.Transaction
import http.handler.dateRangeHandler
import http.handler.transactionsHandler
import io.kotest.core.spec.style.FunSpec
import dao.Entity
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.kotest.shouldHaveBody
import unit.fixtures.aDebitTransaction
import unit.fixtures.entity
import unit.fixtures.withADateOf
import java.time.LocalDate

class DataFilterHandlerTest : FunSpec({

    test("can get a date range") {
        val dateRanges = { listOf(DateRange(StartDate.of(2020, 1, 1), EndDate.of(2021, 1, 1))) }
        val handler = dateRangeHandler(dateRanges)

        handler(Request(Method.GET, "/")) shouldHaveBody dateRanges().asJson()
    }

    test("can use date ranges with filter transactions") {
        val transactions = { dateRange: DateRange ->
            listOf(
                entity { aDebitTransaction().withADateOf(dateRange.startDate.value) },
                entity { aDebitTransaction().withADateOf(dateRange.endDate.value) },
            )
        }
        val startDate = LocalDate.of(2020, 1, 1)
        val endDate = LocalDate.of(2021, 1, 1)
        val handler = transactionsHandler(transactions)

        val request = Request(Method.GET, "/").query("start", startDate.toString()).query("end", endDate.toString())
        handler(request) shouldHaveBody transactions.withDateRange(startDate, endDate).asJson()
    }

})

private fun <T> T.asJson(): String =
    CustomJackson.mapper.writeValueAsString(this)

private fun ((DateRange) -> List<Entity<Transaction>>).withDateRange(startDate: LocalDate, endDate: LocalDate) =
    this(DateRange(StartDate(startDate), EndDate(endDate)))