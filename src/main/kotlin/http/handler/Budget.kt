package http.handler

import domain.Budget
import domain.BudgetReport
import domain.DateRange
import http.lense.budgetLens
import http.lense.budgetReportListLens
import http.lense.createdIdLens
import http.lense.endDateQuery
import http.lense.startDateQuery
import http.model.CreatedId
import org.http4k.core.HttpHandler
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import java.util.*

fun postBudgetHandler(saveBudget: (Budget) -> UUID): HttpHandler = { request ->
    val budget = budgetLens(request)
    val id = saveBudget(budget)
    Response(Status.CREATED).with(createdIdLens of CreatedId(id))
}

fun budgetReportsHandler(createReports: (DateRange) -> List<BudgetReport>): HttpHandler = { request ->
    val startDate = startDateQuery(request)
    val endDate = endDateQuery(request)
    val reports = createReports(DateRange(startDate, endDate))
    Response(Status.OK).with(budgetReportListLens of reports)
}