package domain

data class BudgetReport(
    val budget: Budget,
    val dateRange: DateRange,
    val spending: Value
)
