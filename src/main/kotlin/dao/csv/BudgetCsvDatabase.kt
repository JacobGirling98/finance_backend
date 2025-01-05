package dao.csv

import domain.Budget
import domain.Category
import domain.Value
import domain.frequencyFrom
import java.time.LocalDateTime
import kotlin.time.Duration

class BudgetCsvDatabase(
    syncPeriod: Duration,
    fileName: String,
    lazyDataLoad: Boolean = false,
    now: () -> LocalDateTime = { LocalDateTime.now() }
) : CsvDatabase<Budget>(syncPeriod, fileName, lazyDataLoad, now) {
    override fun headers(): String = "category,value,frequency"

    override fun Budget.toRow(): String = "${category.value},${value.value},${frequency.value}"

    override fun domainFromCommaSeparatedList(row: List<String>): Budget {
        val budget = try {
            Budget(
                Category(row[indexOfColumn("category")]),
                Value.of(row[indexOfColumn("value")].toDouble()),
                frequencyFrom(row[indexOfColumn("frequency")])
            )
        } catch (e: Exception) {
            println("Failed to parse row: $row")
            throw e
        }
        return budget
    }
}