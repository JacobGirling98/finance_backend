package domain

import http.lense.StartDate
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
)

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
    private val year: Int,
    private val month: Int,
    private val day: Int
) {
    val value: LocalDate = LocalDate.of(year, month, day)

    fun previousFiscalMonth(): StartDate = value.minusMonths(1).let { StartDate.of(it.year, it.monthValue, 15) }
}