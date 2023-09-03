package dao.csv

import dao.LoginDatabase
import java.time.LocalDate
import kotlin.time.Duration

class LoginCsvDatabase(syncPeriod: Duration, fileLoc: String) : DateCsvDatabase(syncPeriod, fileLoc), LoginDatabase {
    override fun lastLogin(): LocalDate? = selectAll().maxByOrNull { it.domain }?.domain
}
