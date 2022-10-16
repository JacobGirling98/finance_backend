package http.models

import domain.*

data class CreditDebit(
    val date: Date,
    val category: Category,
    val value: Value,
    val description: Description,
    val quantity: Quantity,
)

data class BankTransfer(
    val date: Date,
    val category: Category,
    val value: Value,
    val description: Description,
    val quantity: Quantity,
    val recipient: Recipient
)

data class PersonalTransfer(
    val date: Date,
    val category: Category,
    val value: Value,
    val description: Description,
    val outbound: Outbound,
    val inbound: Inbound
)

data class Income(
    val date: Date,
    val category: Category,
    val value: Value,
    val description: Description,
    val source: Source
)