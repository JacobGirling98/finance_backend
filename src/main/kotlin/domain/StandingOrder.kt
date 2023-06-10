package domain

data class StandingOrder(
    val nextDate: Date,
    val frequency: Frequency,
    val category: Category,
    val value: Value,
    val description: Description,
    val type: TransactionType,
    val outgoing: Outgoing,
    val quantity: Quantity = Quantity(1),
    val recipient: Recipient? = null,
    val inbound: Inbound? = null,
    val outbound: Outbound? = null,
    val source: Source? = null
)