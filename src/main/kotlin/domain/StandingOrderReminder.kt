package domain

data class StandingOrderReminder(
    val nextReminder: NextReminder,
    val frequency: Frequency,
    val frequencyQuantity: FrequencyQuantity
) : Comparable<StandingOrderReminder> {
    override fun compareTo(other: StandingOrderReminder): Int = nextReminder.value.compareTo(other.nextReminder.value)

}