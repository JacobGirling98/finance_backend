package dao.csv

import kotlin.time.Duration

open class StringCsvDatabase(syncPeriod: Duration, fileLoc: String) :
    SingleValueCsvDatabase<String>(syncPeriod, fileLoc) {

    override fun domainFromCommaSeparatedList(row: List<String>): String = row[indexOfColumn("value")]

    override fun String.toRow(): String = this
}
