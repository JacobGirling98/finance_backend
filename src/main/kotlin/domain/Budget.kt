package domain

data class Budget(
    val category: Category,
    val value: Value,
    val frequency: Frequency
) : Comparable<Budget> {
    override fun compareTo(other: Budget) = category.value.compareTo(other.category.value)
}
