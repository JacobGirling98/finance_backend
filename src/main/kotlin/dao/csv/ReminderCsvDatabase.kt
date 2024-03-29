package dao.csv

import domain.Date
import domain.Description
import domain.FrequencyQuantity
import domain.Reminder
import domain.frequencyFrom
import java.time.LocalDateTime
import kotlin.time.Duration

class ReminderCsvDatabase(
    syncPeriod: Duration,
    fileName: String,
    now: () -> LocalDateTime = { LocalDateTime.now() }
) : CsvDatabase<Reminder>(syncPeriod, fileName, now) {
    override fun headers(): String =
        "next_reminder,frequency_unit,frequency_quantity,description"

    override fun domainFromCommaSeparatedList(row: List<String>): Reminder = Reminder(
        Date(row[indexOfColumn("next_reminder")].toDate()),
        frequencyFrom(row[indexOfColumn("frequency_unit")]),
        FrequencyQuantity(row[indexOfColumn("frequency_quantity")].toInt()),
        Description(row[indexOfColumn("description")])
    )

    override fun Reminder.toRow(): String =
        "${date.value},${frequency.value},${frequencyQuantity.value},${description.value}"
}
