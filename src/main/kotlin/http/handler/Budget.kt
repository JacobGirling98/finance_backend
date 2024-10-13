package http.handler

import domain.Budget
import http.lense.budgetLens
import http.lense.createdIdLens
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