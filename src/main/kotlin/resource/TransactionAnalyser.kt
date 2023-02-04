package resource

import domain.DateRange
import domain.Transaction
import domain.TransactionType
import domain.Value

fun List<Transaction>.spendingBetween(dates: DateRange): Value = valueBetween(dates) { it.outgoing.value }

fun List<Transaction>.incomeBetween(dates: DateRange): Value = valueBetween(dates) { it.type == TransactionType.INCOME }

fun List<Transaction>.savingsBetween(dates: DateRange): Value =
    valueBetween(dates) { it.type == TransactionType.PERSONAL_TRANSFER }

fun List<Transaction>.netIncomeBetween(dates: DateRange): Value =
    this.incomeBetween(dates) - this.spendingBetween(dates)

private fun List<Transaction>.valueBetween(dates: DateRange, predicate: (_: Transaction) -> Boolean): Value =
    filter(dates).filter { predicate(it) }.map { it.value }.reduce { acc, value -> acc + value }
