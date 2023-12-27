package dao.csv

import domain.*
import kotlin.time.Duration

class ReminderCsvDatabase(
    syncPeriod: Duration,
    fileName: String
) : CsvDatabase<Reminder>(syncPeriod, fileName) {
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