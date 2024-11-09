package helpers.fixtures

import domain.Budget
import domain.BudgetReport

fun aBudget() = Budget(category, value)

fun aBudgetReport() = BudgetReport(aBudget(), aDateRange(), value)