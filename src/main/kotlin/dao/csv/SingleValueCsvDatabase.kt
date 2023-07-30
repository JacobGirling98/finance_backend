package dao.csv

import kotlin.time.Duration

abstract class SingleValueCsvDatabase<Domain>(
    syncPeriod: Duration,
    fileLoc: String
) : CsvDatabase<Domain>(syncPeriod, fileLoc) {

    override fun headers(): String = "value"
}