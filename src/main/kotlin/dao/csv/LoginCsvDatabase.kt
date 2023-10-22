package dao.csv

import dao.LoginDatabase
import java.time.LocalDate
import kotlin.time.Duration

class LoginCsvDatabase(syncPeriod: Duration, fileLoc: String) : DateCsvDatabase(syncPeriod, fileLoc), LoginDatabase {
    override fun lastLogin(): LocalDate? = selectAll().sortedByDescending { it.domain }.drop(1).firstOrNull()?.domain
}
