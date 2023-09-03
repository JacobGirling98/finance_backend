package dao.csv

import java.time.LocalDate
import kotlin.time.Duration

open class DateCsvDatabase(syncPeriod: Duration, fileLoc: String) :
    SingleValueCsvDatabase<LocalDate>(syncPeriod, fileLoc) {
    override fun LocalDate.toRow(): String = this.toString()

    override fun domainFromCommaSeparatedList(row: List<String>): LocalDate = row[indexOfColumn("value")].toDate()
}
