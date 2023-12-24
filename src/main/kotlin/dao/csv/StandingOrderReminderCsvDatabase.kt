package dao.csv

import domain.FrequencyQuantity
import domain.NextReminder
import domain.StandingOrderReminder
import domain.frequencyFrom
import kotlin.time.Duration

class StandingOrderReminderCsvDatabase(
    syncPeriod: Duration,
    fileName: String
) : CsvDatabase<StandingOrderReminder>(syncPeriod, fileName) {
    override fun headers(): String =
        "next_reminder,frequency_unit,frequency_quantity"

    override fun domainFromCommaSeparatedList(row: List<String>): StandingOrderReminder = StandingOrderReminder(
        NextReminder(row[indexOfColumn("next_reminder")].toDate()),
        frequencyFrom(row[indexOfColumn("frequency_unit")]),
        FrequencyQuantity(row[indexOfColumn("frequency_quantity")].toInt())
    )

    override fun StandingOrderReminder.toRow(): String =
        "${nextReminder.value},${frequency.value},${frequencyQuantity.value}"

}