package unit.fixtures

import domain.*

fun aDebitStandingOrder() = StandingOrder(
    date,
    FrequencyQuantity(1),
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
    FrequencyQuantity(1),
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
    FrequencyQuantity(1),
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
    FrequencyQuantity(1),
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

fun anIncomeStandingOrder() = StandingOrder(
    date,
    FrequencyQuantity(1),
    Frequency.MONTHLY,
    category,
    value,
    description,
    TransactionType.INCOME,
    Outgoing(false),
    quantity,
    source = source
)
