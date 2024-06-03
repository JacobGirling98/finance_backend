package domain

import java.math.BigDecimal
import java.time.LocalDate

interface TinyType<T> {
    val value: T
}

data class Date(override val value: LocalDate) : TinyType<LocalDate> {
    fun nextFiscalMonth(): Date = value.plusMonths(1).let { Date(LocalDate.of(it.year, it.monthValue, 15)) }

    companion object {
        fun of(year: Int, month: Int, day: Int) = Date(LocalDate.of(year, month, day))
    }
}

data class Category(override val value: String) : TinyType<String>

data class Value(override val value: BigDecimal) : TinyType<BigDecimal> {
    operator fun plus(other: Value) = Value(value.add(other.value))

    operator fun minus(other: Value) = Value(value.minus(other.value))

    companion object {
        fun of(value: Double) = Value(BigDecimal.valueOf(value))
    }
}

data class Description(override val value: String) : TinyType<String>

data class Quantity(override val value: Int) : TinyType<Int>

data class Recipient(override val value: String) : TinyType<String>

data class Inbound(override val value: String) : TinyType<String>

data class Outbound(override val value: String) : TinyType<String>

data class Source(override val value: String) : TinyType<String>

data class Outgoing(override val value: Boolean) : TinyType<Boolean>

data class EndDate(override val value: LocalDate) : TinyType<LocalDate> {
    fun previousFiscalMonth(): StartDate = value.minusMonths(1).let { StartDate.of(it.year, it.monthValue, 15) }

    companion object {
        fun of(year: Int, month: Int, day: Int) = EndDate(LocalDate.of(year, month, day))
    }
}

data class StartDate(override val value: LocalDate) : TinyType<LocalDate> {
    fun nextMonth(): EndDate = value.plusMonths(1).let { EndDate.of(it.year, it.monthValue, it.dayOfMonth) }
    fun nextYear(): EndDate = value.plusYears(1).let { EndDate.of(it.year, it.monthValue, it.dayOfMonth) }
    fun nextFiscalMonth(): EndDate = value.plusMonths(1).let { EndDate.of(it.year, it.monthValue, 15) }
    fun nextFiscalYear(): EndDate = EndDate.of(value.plusYears(1).year, 4, 15)

    companion object {
        fun of(year: Int, month: Int, day: Int) = StartDate(LocalDate.of(year, month, day))
    }
}

data class Login(override val value: LocalDate) : TinyType<LocalDate> {
    companion object {
        fun of(year: Int, month: Int, day: Int) = Login(LocalDate.of(year, month, day))
    }
}

data class FrequencyQuantity(override val value: Int) : TinyType<Int>

data class PageNumber(override val value: Int) : TinyType<Int>

data class PageSize(override val value: Int) : TinyType<Int>

data class TotalElements(override val value: Int) : TinyType<Int>

data class TotalPages(override val value: Int) : TinyType<Int>

data class HasPreviousPage(override val value: Boolean) : TinyType<Boolean>

data class HasNextPage(override val value: Boolean) : TinyType<Boolean>

data class AddedBy(override val value: String) : TinyType<String>

data class NextReminder(override val value: LocalDate) : TinyType<LocalDate>
