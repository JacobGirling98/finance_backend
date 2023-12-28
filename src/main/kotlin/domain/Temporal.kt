package domain

import domain.Frequency.MONTHLY
import domain.Frequency.WEEKLY

interface Temporal {
    val date: Date
    val frequency: Frequency
    val frequencyQuantity: FrequencyQuantity

    fun nextDate(): Date = when (frequency) {
        MONTHLY -> Date(date.value.plusMonths(frequencyQuantity.value.toLong()))
        WEEKLY -> Date(date.value.plusWeeks(frequencyQuantity.value.toLong()))
    }
}