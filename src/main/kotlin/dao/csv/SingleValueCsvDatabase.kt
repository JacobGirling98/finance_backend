package dao.csv

import kotlin.time.Duration

abstract class SingleValueCsvDatabase<Domain : Comparable<Domain>>(
    syncPeriod: Duration,
    fileLoc: String
) : CsvDatabase<Domain>(syncPeriod, fileLoc) {

    override fun headers(): String = "value"
}
