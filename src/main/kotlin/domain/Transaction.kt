package domain


data class Transaction(
    val date: Date,
    val category: Category,
    val value: Value,
    val description: Description,
    val type: TransactionType,
    val outgoing: Boolean,
    val quantity: Quantity = Quantity(1),
    val recipient: Recipient? = null,
    val inbound: Inbound? = null,
    val outbound: Outbound? = null,
    val source: Source? = null
)

