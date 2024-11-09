package resource

import dao.UUIDDatabase
import domain.Budget
import domain.BudgetReport
import domain.DateRange
import domain.Value

class BudgetCalculator(
    private val transactionsProcessor: TransactionProcessor,
    private val budgetDatabase: UUIDDatabase<Budget>
) {

    fun calculateSpendingFor(budget: Budget, dateRange: DateRange): BudgetReport {
        val transactions = transactionsProcessor.transactionsBy(budget.category, dateRange)
        val sum = transactions.sumOf { it.domain.value.value }
        return BudgetReport(budget, dateRange, Value(sum))
    }

    fun calculateBudgets(dateRange: DateRange): List<BudgetReport> =
        budgetDatabase.selectAll().map { budgetEntity -> calculateSpendingFor(budgetEntity.domain, dateRange) }
}