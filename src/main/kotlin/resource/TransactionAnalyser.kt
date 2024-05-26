package resource

import dao.Entity
import domain.Date
import domain.DateRange
import domain.Transaction
import domain.TransactionType
import domain.Value
import java.math.BigDecimal

fun List<Transaction>.spendingBetween(dates: DateRange): Value = valueBetween(dates) { it.outgoing.value }

fun List<Transaction>.incomeBetween(dates: DateRange): Value = valueBetween(dates) { it.type == TransactionType.INCOME }

fun List<Transaction>.savingsBetween(dates: DateRange): Value =
    valueBetween(dates) { it.type == TransactionType.PERSONAL_TRANSFER }

fun List<Transaction>.netIncomeBetween(dates: DateRange): Value =
    this.incomeBetween(dates) - this.spendingBetween(dates)

fun List<Transaction>.mostRecent(): Date? = maxByOrNull { it.date.value }?.date

fun List<Entity<Transaction>>.search(term: String): List<Entity<Transaction>> = filter { it.domain.anyMatch(term) }

private fun List<Transaction>.valueBetween(dates: DateRange, predicate: (_: Transaction) -> Boolean): Value =
    filter(dates).filter { predicate(it) }.map { it.value }.reduceOrNull { acc, value -> acc + value } ?: Value(
        BigDecimal.ZERO
    )
