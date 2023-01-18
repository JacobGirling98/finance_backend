package resource

import domain.*
import java.time.LocalDate

fun monthsOf(transactions: () -> List<Transaction>): () -> List<DateRange> = {
    transactions().distinctDatesBy { it.monthAndYear }.map {
        StartDate(it.value.year, it.value.monthValue, 1).let { startDate ->
            DateRange(
                startDate,
                startDate.nextMonth()
            )
        }
    }
}

fun yearsOf(transactions: () -> List<Transaction>): () -> List<DateRange> = {
    transactions().distinctDatesBy { it.value.year }.map {
        StartDate(it.value.year, 1, 1).let { startDate ->
            DateRange(
                startDate,
                startDate.nextYear()
            )
        }
    }
}

fun fiscalMonthsOf(transactions: () -> List<Transaction>): () -> List<DateRange> = {
    val dateRanges = mutableListOf<DateRange>()
    val data = transactions()

    data.wageDates()
        .fillMissingFiscalMonths()
        .sortedBy { it.value }
        .forEach { dateRanges.add(it, StartDate::nextFiscalMonth) }

    val transactionsWithoutWages = data.filterNot { it.category.value == "Wages" }
    while (transactionsWithoutWages.afterOrEqual(dateRanges.latest()).isNotEmpty()) {
        dateRanges.add(nextDateRange(dateRanges.latest()))
    }
    while (transactionsWithoutWages.beforeOrEqual(dateRanges.earliest()).isNotEmpty()) {
        dateRanges.add(previousDateRange(dateRanges.earliest()))
    }

    dateRanges.sortedBy { it.startDate.value }
}

fun fiscalYearsOf(transactions: () -> List<Transaction>): () -> List<DateRange> = {
    val dateRanges = mutableListOf<DateRange>()
    val data = transactions()

    data.distinctDatesBy { it.value.year }
        .map {
            data.wageOfMonth(4, it.value.year)?.date
                ?: Date(LocalDate.of(it.value.year, 4, 15))
        }
        .forEach { dateRanges.add(it, StartDate::nextFiscalYear) }

    dateRanges
}

private fun MutableList<DateRange>.add(date: Date, nextDate: (StartDate) -> EndDate) {
    val startDate = StartDate(date.value.year, date.value.monthValue, date.value.dayOfMonth)
    if (isNotEmpty() && startDate.value.dayOfMonth != 15) {
        this[size - 1] = last().withEndDateDayOf(startDate.value.dayOfMonth)
    }
    add(DateRange(startDate, nextDate(startDate)))
}

private fun previousDateRange(earliestDateRange: DateRange): DateRange =
    earliestDateRange.startDate.value.let { previousStart ->
        EndDate(
            previousStart.year, previousStart.monthValue, previousStart.dayOfMonth
        ).let { endDate ->
            DateRange(
                endDate.previousFiscalMonth(),
                endDate
            )
        }
    }

private fun nextDateRange(latestDateRange: DateRange): DateRange =
    latestDateRange.endDate.value.let { previousEnd ->
        StartDate(
            previousEnd.year, previousEnd.monthValue, previousEnd.dayOfMonth
        ).let { startDate ->
            DateRange(
                startDate,
                startDate.nextFiscalMonth()
            )
        }
    }

private fun List<Transaction>.wageOfMonth(month: Int, year: Int): Transaction? =
    firstOrNull { transaction -> transaction.category.value == "Wages" && transaction.date.value.monthValue == month && transaction.date.value.year == year }

private fun MutableList<DateRange>.latest(): DateRange = maxBy { it.endDate.value }

private fun MutableList<DateRange>.earliest(): DateRange = minBy { it.startDate.value }

private fun List<Transaction>.beforeOrEqual(dateRange: DateRange) =
    filter { it.date.value.let { date -> date.isBefore(dateRange.startDate.value) || date.isEqual(dateRange.startDate.value) } }

private fun List<Transaction>.afterOrEqual(dateRange: DateRange) =
    filter { it.date.value.let { date -> date.isAfter(dateRange.endDate.value) || date.isEqual(dateRange.endDate.value) } }

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

private fun <T> List<Transaction>.distinctDatesBy(field: (Date) -> T): List<Date> = map { it.date }.distinctBy(field)

private fun List<Transaction>.wageDates() =
    filter { it.category.value == "Wages" }.map { it.date }

private val Date.monthAndYear: Pair<Int, Int>
    get() = Pair(value.monthValue, value.year)