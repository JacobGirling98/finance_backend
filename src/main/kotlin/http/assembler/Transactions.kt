package http.assembler

import domain.Outgoing
import domain.Quantity
import domain.Transaction
import domain.TransactionType
import domain.TransactionType.BANK_TRANSFER
import domain.TransactionType.INCOME
import domain.TransactionType.PERSONAL_TRANSFER

fun transactionFrom(transaction: http.model.Transaction.CreditDebit, transactionType: TransactionType) = Transaction(
    transaction.date,
    transaction.category,
    transaction.value,
    transaction.description,
    transactionType,
    Outgoing(true),
    quantity = transaction.quantity
)

fun transactionFrom(transaction: http.model.Transaction.BankTransfer) = Transaction(
    transaction.date,
    transaction.category,
    transaction.value,
    transaction.description,
    BANK_TRANSFER,
    Outgoing(true),
    quantity = transaction.quantity,
    recipient = transaction.recipient
)

fun transactionFrom(transaction: http.model.Transaction.PersonalTransfer) = Transaction(
    transaction.date,
    transaction.category,
    transaction.value,
    transaction.description,
    PERSONAL_TRANSFER,
    Outgoing(false),
    quantity = Quantity(1),
    outbound = transaction.outbound,
    inbound = transaction.inbound
)

fun transactionFrom(transaction: http.model.Transaction.Income) = Transaction(
    transaction.date,
    transaction.category,
    transaction.value,
    transaction.description,
    INCOME,
    Outgoing(false),
    quantity = Quantity(1),
    source = transaction.source
)