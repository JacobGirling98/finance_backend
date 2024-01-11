package resource

import dao.Entity
import domain.EndDate
import domain.StartDate
import domain.Transaction
import domain.TransactionType

fun toFilter(
    to: EndDate? = null,
    from: StartDate? = null,
    type: TransactionType? = null
): (Entity<Transaction>) -> Boolean {
    if (to == null && from == null && type == null) throw IllegalStateException("At least one filter should be provided")

    val fromCondition: (Entity<Transaction>) -> Boolean = if (from != null) {
        { it.domain.date.value.isAfter(from.value) || it.domain.date.value == from.value }
    } else {
        { true }
    }

    val toCondition: (Entity<Transaction>) -> Boolean = if (to != null) {
        { it.domain.date.value.isBefore(to.value) }
    } else {
        { true }
    }

    val typeCondition: (Entity<Transaction>) -> Boolean = if (type != null) {
        { it.domain.type == type }
    } else {
        { true }
    }

    return { fromCondition(it) && toCondition(it) && typeCondition(it) }
}