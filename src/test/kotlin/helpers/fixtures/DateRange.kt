package helpers.fixtures

import domain.DateRange
import domain.EndDate
import domain.StartDate

fun aDateRange() = DateRange(StartDate(date.value), EndDate(date.value))