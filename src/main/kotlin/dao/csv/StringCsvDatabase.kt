package dao.csv

import java.time.LocalDateTime
import kotlin.time.Duration

open class StringCsvDatabase(
    syncPeriod: Duration,
    fileLoc: String,
    now: () -> LocalDateTime = { LocalDateTime.now() }
) : SingleValueCsvDatabase<String>(syncPeriod, fileLoc, now) {

    override fun domainFromCommaSeparatedList(row: List<String>): String = row[indexOfColumn("value")]

    override fun String.toRow(): String = this
}
