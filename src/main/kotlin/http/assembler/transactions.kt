package http.assembler

import domain.Outgoing
import domain.Transaction
import domain.TransactionType
import http.models.Credit

fun transactionFrom(transaction: Credit) = Transaction(
    transaction.date,
    transaction.category,
    transaction.value,
    transaction.description,
    TransactionType.CREDIT,
    Outgoing(true),
    quantity = transaction.quantity
)