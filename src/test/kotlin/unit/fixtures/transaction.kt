package unit.fixtures

import domain.*
import domain.TransactionType.*
import java.math.BigDecimal
import java.time.LocalDate

fun aDebitTransaction() = Transaction(
    date,
    category,
    value,
    description,
    DEBIT,
    Outgoing(true),
    quantity,
    null,
    null,
    null,
    null
)

fun aWagesIncome() = Transaction(
    date,
    Category("Wages"),
    value,
    description,
    INCOME,
    Outgoing(false),
    quantity,
    null,
    null,
    null,
    Source("Work")
)

fun aPersonalTransferTransaction() = Transaction(
    date,
    category,
    value,
    description,
    PERSONAL_TRANSFER,
    Outgoing(false),
    quantity,
    null,
    inbound,
    outbound,
    null
)

fun Transaction.withAValueOf(value: Double) = copy(value = Value(BigDecimal.valueOf(value)))

fun Transaction.withADateOf(year: Int = 2020, month: Int = 1, day: Int = 1) =
    copy(date = Date(LocalDate.of(year, month, day)))

fun Transaction.withADateOf(date: LocalDate) =
    copy(date = Date(date))

fun Transaction.withADescriptionOf(value: String) = copy(description = Description(value))