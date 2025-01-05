package domain

enum class Frequency(val value: String) {
    WEEKLY("weekly"), MONTHLY("monthly"), YEARLY("yearly")
}

fun frequencyFrom(value: String): Frequency = Frequency.entries.first { it.value.lowercase() == value.lowercase() }
