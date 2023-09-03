package resource

import domain.DateRange
import domain.Transaction

fun List<Transaction>.filter(dateRange: DateRange) = filter {
    it.date.value.let { date ->
        date.isEqual(dateRange.startDate.value) || (date.isAfter(dateRange.startDate.value) && date.isBefore(dateRange.endDate.value))
    }
}
