package domain

data class StandingOrder(
    val nextDate: Date,
    val frequencyQuantity: FrequencyQuantity,
    val frequencyUnit: Frequency,
    val category: Category,
    val value: Value,
    val description: Description,
    val type: TransactionType,
    val outgoing: Outgoing,
    val quantity: Quantity = Quantity(1),
    val recipient: Recipient? = null,
    val inbound: Inbound? = null,
    val outbound: Outbound? = null
) : Comparable<StandingOrder> {
    override fun compareTo(other: StandingOrder): Int = nextDate.value.compareTo(other.nextDate.value)
}
