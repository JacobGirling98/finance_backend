package domain

import domain.Frequency.*

interface Temporal {
    val date: Date
    val frequency: Frequency
    val frequencyQuantity: FrequencyQuantity

    fun nextDate(): Date = when (frequency) {
        MONTHLY -> Date(date.value.plusMonths(frequencyQuantity.value.toLong()))
        WEEKLY -> Date(date.value.plusWeeks(frequencyQuantity.value.toLong()))
        YEARLY -> Date(date.value.plusYears(frequencyQuantity.value.toLong()))
    }
}
