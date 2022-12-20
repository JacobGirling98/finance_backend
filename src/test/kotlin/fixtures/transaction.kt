package fixtures

import domain.Outgoing
import domain.Transaction
import domain.TransactionType.DEBIT
import domain.Value
import java.math.BigDecimal

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

fun Transaction.withAValueOf(value: Double) = copy(value = Value(BigDecimal.valueOf(value)))