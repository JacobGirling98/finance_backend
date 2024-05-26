package http.assembler

import domain.AddedBy
import domain.Outgoing
import domain.Quantity
import domain.Transaction
import domain.TransactionType
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
    TransactionType.BANK_TRANSFER,
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
    TransactionType.PERSONAL_TRANSFER,
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
    TransactionType.INCOME,
    Outgoing(false),
    quantity = Quantity(1),
    source = transaction.source,
    addedBy = addedBy
)
