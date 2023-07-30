package unit.fixtures

import domain.Frequency
import domain.Outgoing
import domain.StandingOrder
import domain.TransactionType

fun aDebitStandingOrder() = StandingOrder(
    date,
    Frequency.MONTHLY,
    category,
    value,
    description,
    TransactionType.DEBIT,
    Outgoing(true),
    quantity,
    null,
    null,
    null
)

fun aCreditStandingOrder() = StandingOrder(
    date,
    Frequency.MONTHLY,
    category,
    value,
    description,
    TransactionType.CREDIT,
    Outgoing(true),
    quantity,
    null,
    null,
    null
)

fun aBankTransferStandingOrder() = StandingOrder(
    date,
    Frequency.MONTHLY,
    category,
    value,
    description,
    TransactionType.BANK_TRANSFER,
    Outgoing(true),
    quantity,
    recipient,
    null,
    null
)

fun aPersonalTransferStandingOrder() = StandingOrder(
    date,
    Frequency.MONTHLY,
    category,
    value,
    description,
    TransactionType.PERSONAL_TRANSFER,
    Outgoing(false),
    quantity,
    null,
    inbound,
    outbound
)