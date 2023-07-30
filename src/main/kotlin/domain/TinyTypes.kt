package domain

import java.math.BigDecimal
import java.time.LocalDate

data class Date(
    val value: LocalDate
) {
    fun nextFiscalMonth(): Date = value.plusMonths(1).let { Date(LocalDate.of(it.year, it.monthValue, 15)) }
}

data class Category(
    val value: String
)

data class Value(
    val value: BigDecimal
) {
    operator fun plus(other: Value) = Value(value.add(other.value))

    operator fun minus(other: Value) = Value(value.minus(other.value))

    companion object {
        fun of(value: Double) = Value(BigDecimal.valueOf(value))
    }
}

data class Description(
    val value: String
)

data class Quantity(
    val value: Int
)

data class Recipient(
    val value: String
)

data class Inbound(
    val value: String
)

data class Outbound(
    val value: String
)

data class Source(
    val value: String
)

data class Outgoing(
    val value: Boolean
)

data class EndDate(
    val value: LocalDate
) {
    fun previousFiscalMonth(): StartDate = value.minusMonths(1).let { StartDate.of(it.year, it.monthValue, 15) }

    companion object {
        fun of(year: Int, month: Int, day: Int) = EndDate(LocalDate.of(year, month, day))
    }
}

data class StartDate(
    val value: LocalDate
) {
    fun nextMonth(): EndDate = value.plusMonths(1).let { EndDate.of(it.year, it.monthValue, it.dayOfMonth) }
    fun nextYear(): EndDate = value.plusYears(1).let { EndDate.of(it.year, it.monthValue, it.dayOfMonth) }
    fun nextFiscalMonth(): EndDate = value.plusMonths(1).let { EndDate.of(it.year, it.monthValue, 15) }
    fun nextFiscalYear(): EndDate = EndDate.of(value.plusYears(1).year, 4, 15)

    companion object {
        fun of(year: Int, month: Int, day: Int) = StartDate(LocalDate.of(year, month, day))
    }
}

data class Login(val value: LocalDate) {
    companion object {
        fun of(year: Int, month: Int, day: Int) = Login(LocalDate.of(year, month, day))
    }
}