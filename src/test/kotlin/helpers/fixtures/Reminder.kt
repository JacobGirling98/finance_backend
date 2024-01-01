package helpers.fixtures

import domain.Date
import domain.Frequency
import domain.FrequencyQuantity
import domain.Reminder
import java.time.LocalDate

fun aReminder() = Reminder(
    date,
    Frequency.MONTHLY,
    FrequencyQuantity(1),
    description
)

fun Reminder.withADateOf(date: LocalDate) = copy(date = Date(date))