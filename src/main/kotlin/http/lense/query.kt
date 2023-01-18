package http.lense

import domain.EndDate
import org.http4k.lens.Query
import org.http4k.lens.localDate
import java.time.LocalDate

data class CustomType(val value: String)

val requiredCustomQuery = Query.localDate().map(::StartDate) { it.value }.required("myCustomType")
val otherQuery = Query.localDate().required("")

data class StartDate(
    val value: LocalDate
) {
    fun nextMonth(): EndDate = value.plusMonths(1).let { EndDate(it.year, it.monthValue, it.dayOfMonth) }
    fun nextYear(): EndDate = value.plusYears(1).let { EndDate(it.year, it.monthValue, it.dayOfMonth) }
    fun nextFiscalMonth(): EndDate = value.plusMonths(1).let { EndDate(it.year, it.monthValue, 15) }
    fun nextFiscalYear(): EndDate = EndDate(value.plusYears(1).year, 4, 15)

    companion object {
        fun of(year: Int, month: Int, day: Int) = StartDate(LocalDate.of(year, month, day))
    }
}