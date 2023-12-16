package domain

import java.time.LocalDate

data class DateRange(
    val startDate: StartDate,
    val endDate: EndDate
) {
    fun withEndDateDayOf(day: Int) =
        copy(endDate = EndDate(LocalDate.of(endDate.value.year, endDate.value.monthValue, day)))
}
