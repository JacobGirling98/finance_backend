package unit.http.handler

import domain.Budget
import domain.BudgetReport
import domain.Category
import domain.DateRange
import domain.EndDate
import domain.StartDate
import domain.Value
import helpers.fixtures.aBudgetReport
import helpers.fixtures.deserialize
import http.handler.budgetReportsHandler
import http.handler.postBudgetHandler
import http.model.CreatedId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status.Companion.CREATED
import org.http4k.core.Status.Companion.OK
import org.http4k.kotest.shouldHaveStatus
import java.util.*

class BudgetHandlerTest : FunSpec({

    test("can create a budget") {
        val uuid = UUID.randomUUID()
        val saveBudget = mockk<(Budget) -> UUID>()
        every { saveBudget(any()) } returns uuid
        val handler = postBudgetHandler(saveBudget)
        val request = Request(Method.POST, "").body(
            """
                {
                    "category": "Food",
                    "value": 200.0
                }
            """.trimIndent()
        )

        val response = handler(request)

        response shouldHaveStatus CREATED
        response.deserialize<CreatedId>().id shouldBe uuid
        verify { saveBudget(Budget(Category("Food"), Value.of(200.00))) }
    }

    test("can get budget reports") {
        val createReports = mockk<(DateRange) -> List<BudgetReport>>()
        val reports = listOf(aBudgetReport())
        every { createReports(any()) } returns reports
        val handler = budgetReportsHandler(createReports)
        val request = Request(Method.GET, "").query("start", "2024-01-01").query("end", "2024-02-01")

        val response = handler(request)

        response shouldHaveStatus OK
        response.deserialize<List<BudgetReport>>() shouldBe reports
        verify { createReports(DateRange(StartDate.of(2024, 1, 1), EndDate.of(2024, 2, 1))) }
    }
})