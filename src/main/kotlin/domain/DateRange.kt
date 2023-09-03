package domain

import java.time.LocalDate

data class DateRange(
    val startDate: StartDate,
    val endDate: EndDate
) {
    fun withEndDateDayOf(day: Int) =
        copy(endDate = endDate.let { it.copy(value = LocalDate.of(it.value.year, it.value.monthValue, day)) })
}
