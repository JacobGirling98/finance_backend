package dao.csv

import java.time.LocalDateTime
import kotlin.time.Duration

abstract class SingleValueCsvDatabase<Domain : Comparable<Domain>>(
    syncPeriod: Duration,
    fileLoc: String,
    lazyDataLoad: Boolean = false,
    now: () -> LocalDateTime = { LocalDateTime.now() }
) : CsvDatabase<Domain>(syncPeriod, fileLoc, lazyDataLoad, now) {

    override fun headers(): String = "value"
}
