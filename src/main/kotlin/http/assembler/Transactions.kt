package http.assembler

import domain.*
import domain.TransactionType.*
import http.model.Transaction.BankTransfer
import http.model.Transaction.CreditDebit
import http.model.Transaction.Income
import http.model.Transaction.PersonalTransfer

fun transactionFrom(transaction: CreditDebit, transactionType: TransactionType, addedBy: AddedBy) = Transaction(
    transaction.date,
    transaction.category,
    transaction.value,
    transaction.description,
    transactionType,
    Outgoing(true),
    quantity = transaction.quantity,
    addedBy = addedBy
)

fun transactionFrom(transaction: BankTransfer, addedBy: AddedBy) = Transaction(
    transaction.date,
    transaction.category,
    transaction.value,
    transaction.description,
    BANK_TRANSFER,
    Outgoing(true),
    quantity = transaction.quantity,
    recipient = transaction.recipient,
    addedBy = addedBy
)

fun transactionFrom(transaction: PersonalTransfer, addedBy: AddedBy) = Transaction(
    transaction.date,
    transaction.category,
    transaction.value,
    transaction.description,
    PERSONAL_TRANSFER,
    Outgoing(false),
    quantity = Quantity(1),
    outbound = transaction.outbound,
    inbound = transaction.inbound,
    addedBy = addedBy
)

fun transactionFrom(transaction: Income, addedBy: AddedBy) = Transaction(
    transaction.date,
    transaction.category,
    transaction.value,
    transaction.description,
    INCOME,
    Outgoing(false),
    quantity = Quantity(1),
    source = transaction.source,
    addedBy = addedBy
)
