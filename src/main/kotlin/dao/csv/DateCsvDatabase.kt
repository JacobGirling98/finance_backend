package dao.csv

import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.time.Duration

open class DateCsvDatabase(syncPeriod: Duration, fileLoc: String, now: () -> LocalDateTime = { LocalDateTime.now() }) :
    SingleValueCsvDatabase<LocalDate>(syncPeriod, fileLoc, now) {
    override fun LocalDate.toRow(): String = this.toString()

    override fun domainFromCommaSeparatedList(row: List<String>): LocalDate = row[indexOfColumn("value")].toDate()
}
