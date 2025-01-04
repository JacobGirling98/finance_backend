package dao.csv

import dao.LoginDatabase
import java.time.LocalDate
import kotlin.time.Duration

class LoginCsvDatabase(syncPeriod: Duration, fileLoc: String, lazyDataLoad: Boolean = false) :
    DateCsvDatabase(syncPeriod, fileLoc, lazyDataLoad = lazyDataLoad), LoginDatabase {
    override fun lastLogin(): LocalDate? = selectAll().sortedByDescending { it.domain }.drop(1).firstOrNull()?.domain
}
