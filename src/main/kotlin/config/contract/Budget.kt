package config.contract

import dao.UUIDDatabase
import domain.Budget
import domain.Category
import domain.Value
import http.asTag
import http.handler.postBudgetHandler
import http.lense.budgetLens
import http.lense.createdIdLens
import http.model.CreatedId
import org.http4k.contract.meta
import org.http4k.core.Method.POST
import org.http4k.core.Status
import java.util.*

private const val BASE_URL = "/budget"
private val tag = BASE_URL.asTag()

fun budgetContracts(database: UUIDDatabase<Budget>) = listOf(
    addBudget { database.save(it) }
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