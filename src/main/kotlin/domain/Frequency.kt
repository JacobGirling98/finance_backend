package domain

enum class Frequency(val value: String) {
    WEEKLY("weekly"), MONTHLY("monthly")
}

fun frequencyFrom(value: String): Frequency = Frequency.values().first { it.value.lowercase() == value.lowercase() }
