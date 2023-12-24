package domain

data class Reminder(
    val nextReminder: Date,
    val frequency: Frequency,
    val frequencyQuantity: FrequencyQuantity,
    val description: Description
) : Comparable<Reminder> {
    override fun compareTo(other: Reminder): Int = nextReminder.value.compareTo(other.nextReminder.value)

}