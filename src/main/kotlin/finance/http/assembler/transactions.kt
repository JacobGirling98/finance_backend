package finance.http.assembler

import finance.domain.*
import finance.domain.TransactionType.*
import finance.http.model.BankTransfer
import finance.http.model.CreditDebit
import finance.http.model.Income
import finance.http.model.PersonalTransfer

fun transactionFrom(transaction: CreditDebit, transactionType: TransactionType) = Transaction(
    transaction.date,
    transaction.category,
    transaction.value,
    transaction.description,
    transactionType,
    Outgoing(true),
    quantity = transaction.quantity
)

fun transactionFrom(transaction: BankTransfer) = Transaction(
    transaction.date,
    transaction.category,
    transaction.value,
    transaction.description,
    BANK_TRANSFER,
    Outgoing(true),
    quantity = transaction.quantity,
    recipient = transaction.recipient
)

fun transactionFrom(transaction: PersonalTransfer) = Transaction(
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

fun transactionFrom(transaction: Income) = Transaction(
    transaction.date,
    transaction.category,
    transaction.value,
    transaction.description,
    INCOME,
    Outgoing(false),
    quantity = Quantity(1),
    source = transaction.source
)