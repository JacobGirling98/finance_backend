package dao.csv

import domain.Budget
import domain.Category
import domain.Value
import java.time.LocalDateTime
import kotlin.time.Duration

class BudgetCsvDatabase(
    syncPeriod: Duration,
    fileName: String,
    now: () -> LocalDateTime = { LocalDateTime.now() }
) : CsvDatabase<Budget>(syncPeriod, fileName, now) {
    override fun headers(): String = "category,value"

    override fun Budget.toRow(): String = "${category.value},${value.value}"

    override fun domainFromCommaSeparatedList(row: List<String>): Budget {
        val budget = try {
            Budget(
                Category(row[indexOfColumn("category")]),
                Value.of(row[indexOfColumn("value")].toDouble())
            )
        } catch (e: Exception) {
            println("Failed to parse row: $row")
            throw e
        }
        return budget
    }
}