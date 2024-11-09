package config.contract

import dao.UUIDDatabase
import domain.Budget
import domain.BudgetReport
import domain.Category
import domain.DateRange
import domain.EndDate
import domain.StartDate
import domain.Value
import http.asTag
import http.handler.budgetReportsHandler
import http.handler.postBudgetHandler
import http.lense.budgetLens
import http.lense.budgetReportListLens
import http.lense.createdIdLens
import http.lense.endDateQuery
import http.lense.startDateQuery
import http.model.CreatedId
import org.http4k.contract.meta
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Status
import resource.BudgetCalculator
import java.util.*

private const val BASE_URL = "/budget"
private val tag = BASE_URL.asTag()

fun budgetContracts(database: UUIDDatabase<Budget>, budgetCalculator: BudgetCalculator) = listOf(
    addBudget { database.save(it) },
    budgetReport { budgetCalculator.calculateBudgets(it) }
)

private fun addBudget(save: (Budget) -> UUID) = BASE_URL meta {
    operationId = "$BASE_URL/post"
    summary = "Add a budget"
    tags += tag
    receiving(
        budgetLens to Budget(
            Category("String"),
            Value.of(1.0)
        )
    )
    returning(Status.CREATED, createdIdLens to CreatedId.random())
} bindContract POST to postBudgetHandler(save)

private fun budgetReport(createReport: (DateRange) -> List<BudgetReport>) = "$BASE_URL/report" meta {
    operationId = "$BASE_URL/report/get"
    summary = "Get budget reports"
    tags += tag
    queries += startDateQuery
    queries += endDateQuery
    returning(
        Status.OK, budgetReportListLens to listOf(
            BudgetReport(
                Budget(Category("String"), Value.of(0.0)), DateRange(
                    StartDate.of(2024, 1, 1), EndDate.of(2024, 2, 1)
                ),
                Value.of(0.0)
            )
        )
    )
} bindContract GET to budgetReportsHandler(createReport)