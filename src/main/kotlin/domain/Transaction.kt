package domain

data class Transaction(
    val date: Date,
    val category: Category,
    val value: Value,
    val description: Description,
    val type: TransactionType,
    val outgoing: Outgoing,
    val quantity: Quantity = Quantity(1),
    val recipient: Recipient? = null,
    val inbound: Inbound? = null,
    val outbound: Outbound? = null,
    val source: Source? = null,
    val addedBy: AddedBy
) : Comparable<Transaction> {
    override fun compareTo(other: Transaction): Int = other.date.value.compareTo(date.value)

    fun anyMatch(searchTerm: String): Boolean {
        val loweredSearchTerm = searchTerm.trim().lowercase()
        return description.value.lowercase().contains(loweredSearchTerm)
                || category.value.lowercase().contains(loweredSearchTerm)
                || recipient?.value?.lowercase()?.contains(loweredSearchTerm) == true
                || inbound?.value?.lowercase()?.contains(loweredSearchTerm) == true
                || outbound?.value?.lowercase()?.contains(loweredSearchTerm) == true
                || source?.value?.lowercase()?.contains(loweredSearchTerm) == true
    }
}

fun List<Transaction>.totalValue() = map { it.value.value }.reduce { total, value -> total.add(value) }.toFloat()
