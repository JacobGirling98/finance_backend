package http.assembler

import domain.Outgoing
import domain.Quantity
import domain.StandingOrder
import domain.TransactionType

fun standingOrderFrom(transaction: http.model.StandingOrder.CreditDebit, transactionType: TransactionType) = StandingOrder(
    transaction.date,
    transaction.frequencyQuantity,
    transaction.frequencyUnit,
    transaction.category,
    transaction.value,
    transaction.description,
    transactionType,
    Outgoing(true),
    quantity = transaction.quantity
)

fun standingOrderFrom(transaction: http.model.StandingOrder.BankTransfer) = StandingOrder(
    transaction.date,
    transaction.frequencyQuantity,
    transaction.frequencyUnit,
    transaction.category,
    transaction.value,
    transaction.description,
    TransactionType.BANK_TRANSFER,
    Outgoing(true),
    quantity = transaction.quantity,
    recipient = transaction.recipient
)

fun standingOrderFrom(transaction: http.model.StandingOrder.PersonalTransfer) = StandingOrder(
    transaction.date,
    transaction.frequencyQuantity,
    transaction.frequencyUnit,
    transaction.category,
    transaction.value,
    transaction.description,
    TransactionType.PERSONAL_TRANSFER,
    Outgoing(false),
    quantity = Quantity(1),
    outbound = transaction.outbound,
    inbound = transaction.inbound
)

fun standingOrderFrom(transaction: http.model.StandingOrder.Income) = StandingOrder(
    transaction.date,
    transaction.frequencyQuantity,
    transaction.frequencyUnit,
    transaction.category,
    transaction.value,
    transaction.description,
    TransactionType.INCOME,
    Outgoing(false),
    quantity = Quantity(1)
    // source = transaction.source
)
