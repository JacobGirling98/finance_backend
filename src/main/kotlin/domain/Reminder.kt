package domain

data class Reminder(
    override val date: Date,
    override val frequency: Frequency,
    override val frequencyQuantity: FrequencyQuantity,
    val description: Description
) : Comparable<Reminder>, Advancable {
    override fun compareTo(other: Reminder): Int = date.value.compareTo(other.date.value)

}