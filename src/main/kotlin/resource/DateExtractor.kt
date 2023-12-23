package resource

import dao.Entity
import domain.*
import java.time.LocalDate

fun monthsOf(transactions: () -> List<Entity<Transaction>>): () -> List<DateRange> = {
    transactions().distinctDatesBy { it.monthAndYear }.map {
        StartDate.of(it.value.year, it.value.monthValue, 1).let { startDate ->
            DateRange(
                startDate,
                startDate.nextMonth()
            )
        }
    }.sortedByDescending { it.startDate.value }
}

fun yearsOf(transactions: () -> List<Entity<Transaction>>): () -> List<DateRange> = {
    transactions().distinctDatesBy { it.value.year }.map {
        StartDate.of(it.value.year, 1, 1).let { startDate ->
            DateRange(
                startDate,
                startDate.nextYear()
            )
        }
    }.sortedByDescending { it.startDate.value }
}

fun fiscalMonthsOf(transactions: () -> List<Entity<Transaction>>): () -> List<DateRange> = {
    val dateRanges = mutableListOf<DateRange>()
    val data = transactions()

    data.wageDates()
        .fillMissingFiscalMonths()
        .sortedBy { it.value }
        .forEach { dateRanges.add(it, StartDate::nextFiscalMonth) }

    val transactionsWithoutWages = data.filterNot { it.domain.category.value == "Wages" }
    while (transactionsWithoutWages.afterOrEqual(dateRanges.latest()).isNotEmpty()) {
        dateRanges.add(nextDateRange(dateRanges.latest()))
    }
    while (transactionsWithoutWages.beforeOrEqual(dateRanges.earliest()).isNotEmpty()) {
        dateRanges.add(previousDateRange(dateRanges.earliest()))
    }

    dateRanges.sortedByDescending { it.startDate.value }
}

fun fiscalYearsOf(transactions: () -> List<Entity<Transaction>>): () -> List<DateRange> = {
    val dateRanges = mutableListOf<DateRange>()
    val data = transactions()

    data.distinctDatesBy { it.value.year }
        .map {
            data.wageOfMonth(4, it.value.year)?.domain?.date
                ?: Date(LocalDate.of(it.value.year, 4, 15))
        }
        .forEach { dateRanges.add(it, StartDate::nextFiscalYear) }

    dateRanges.sortedByDescending { it.startDate.value }
}

private fun MutableList<DateRange>.add(date: Date, nextDate: (StartDate) -> EndDate) {
    val startDate = StartDate.of(date.value.year, date.value.monthValue, date.value.dayOfMonth)
    if (isNotEmpty() && startDate.value.dayOfMonth != 15) {
        this[size - 1] = last().withEndDateDayOf(startDate.value.dayOfMonth)
    }
    add(DateRange(startDate, nextDate(startDate)))
}

private fun previousDateRange(earliestDateRange: DateRange): DateRange =
    earliestDateRange.startDate.value.let { previousStart ->
        EndDate.of(
            previousStart.year,
            previousStart.monthValue,
            previousStart.dayOfMonth
        ).let { endDate ->
            DateRange(
                endDate.previousFiscalMonth(),
                endDate
            )
        }
    }

private fun nextDateRange(latestDateRange: DateRange): DateRange =
    latestDateRange.endDate.value.let { previousEnd ->
        StartDate.of(
            previousEnd.year,
            previousEnd.monthValue,
            previousEnd.dayOfMonth
        ).let { startDate ->
            DateRange(
                startDate,
                startDate.nextFiscalMonth()
            )
        }
    }

private fun List<Entity<Transaction>>.wageOfMonth(month: Int, year: Int): Entity<Transaction>? =
    firstOrNull { transaction -> transaction.domain.category.value == "Wages" && transaction.domain.date.value.monthValue == month && transaction.domain.date.value.year == year }

private fun MutableList<DateRange>.latest(): DateRange = maxBy { it.endDate.value }

private fun MutableList<DateRange>.earliest(): DateRange = minBy { it.startDate.value }

private fun List<Entity<Transaction>>.beforeOrEqual(dateRange: DateRange) =
    filter { it.domain.date.value.let { date -> date.isBefore(dateRange.startDate.value) || date.isEqual(dateRange.startDate.value) } }

private fun List<Entity<Transaction>>.afterOrEqual(dateRange: DateRange) =
    filter { it.domain.date.value.let { date -> date.isAfter(dateRange.endDate.value) || date.isEqual(dateRange.endDate.value) } }

private fun List<Date>.fillMissingFiscalMonths(): List<Date> {
    val sortedDates = this.sortedBy { it.value }.toMutableList()
    val filledDates = mutableListOf<Date>()
    for (index in sortedDates.indices) {
        filledDates.add(sortedDates[index])
        if (index != sortedDates.size - 1) {
            while (sortedDates[index + 1].isMoreThanAMonthAfter(filledDates.last())) {
                filledDates.add(filledDates.last().nextFiscalMonth())
            }
        }
    }
    return filledDates
}

private fun Date.isMoreThanAMonthAfter(other: Date): Boolean {
    val newDate = other.value.plusMonths(1)
    return if (this.value.isBefore(other.value)) false else newDate.monthValue != this.value.monthValue
}

private fun <T> List<Entity<Transaction>>.distinctDatesBy(field: (Date) -> T): List<Date> =
    map { it.domain.date }.distinctBy(field)

private fun List<Entity<Transaction>>.wageDates() =
    filter { it.domain.category.value == "Wages" }.map { it.domain.date }

private val Date.monthAndYear: Pair<Int, Int>
    get() = Pair(value.monthValue, value.year)
