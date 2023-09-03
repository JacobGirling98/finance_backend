package resource

import dao.Database
import dao.asEntity
import java.time.LocalDate
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class LoginSynchroniser(private val loginDatabase: Database<LocalDate, UUID>) {

    private val lock = ReentrantLock()

    fun addLogin(localDate: LocalDate) {
        lock.withLock {
            val logins = loginDatabase.selectAll()
            if (logins.none { it.domain == localDate }) {
                val newId = loginDatabase.save(localDate)
                val allLogins = (logins + localDate.asEntity(newId)).sortedByDescending { it.domain }
                val loginsToRemove = allLogins.drop(3)
                loginsToRemove.forEach { loginDatabase.delete(it.id) }
            }
        }
    }
}
