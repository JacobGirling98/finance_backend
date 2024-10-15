package script

import config.categoryDatabase
import config.transactionDatabase
import dao.AuditableEntity
import domain.Transaction
import java.math.BigDecimal
import java.time.LocalDate
import java.time.Month

fun main() {
    val dates = (1L .. 12).map { LocalDate.now().minusMonths(it) }
    val months = dates.map { it.month }
    val earliestDate = dates.last()

    val transactions = transactionDatabase.selectAll().filter { it.domain.date.value.isAfter(earliestDate) }

    val categories = categoryDatabase.selectAll().map { it.domain }.filterNot { it in listOf("Wages", "Savings") }

    val spending = months.map { month ->
        spendingPerCategory(dataInMonth(month, transactions), categories)
    }

    val mapValues = spending.flatMap { it.entries }
        .groupBy { it.key }
        .mapValues { (_, values) -> values.map { it.value } }
        .mapValues { (_, values) -> values.average() }

    mapValues.toList().sortedBy { it.second.negate() }.forEach { println("${it.first}: ${it.second}") }
}

private fun spendingPerCategory(data: Transactions, categories: List<String>): Map<String, BigDecimal> =
    categories.associateWith { spendingFor(it, data) }

private fun spendingFor(category: String, data: Transactions): BigDecimal =
    data.filter { it.domain.category.value == category }.sumOf { it.domain.value.value }

private fun dataInMonth(month: Month, data: Transactions): Transactions =
    data.filter { it.domain.date.value.month == month }

private fun List<BigDecimal>.average() = fold(BigDecimal.ZERO) { acc, value -> acc + value } / BigDecimal.valueOf(size.toLong())

typealias Transactions = List<AuditableEntity<Transaction>>