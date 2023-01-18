package domain

import http.lense.StartDate

data class DateRange(
    val startDate: StartDate,
    val endDate: EndDate
) {
    fun withEndDateDayOf(day: Int) = copy(endDate = endDate.copy(day = day))
}
