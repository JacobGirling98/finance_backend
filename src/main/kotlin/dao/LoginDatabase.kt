package dao

import java.io.File
import java.time.LocalDate
import java.util.concurrent.locks.ReentrantLock

data class Login(
    val value: LocalDate
)

class LoginDatabase(
    filePath: String
) : Database<Login> {

    private val file = File("$filePath/logins.txt")
    private var logins: MutableSet<Login> = mutableSetOf()
    private val lock = ReentrantLock()

    fun initialise() {
        logins.addAll(file.readLines().map { Login(LocalDate.parse(it)) })
    }

    override fun save(data: Login) {
        if (!logins.contains(data)) {
            logins.add(data)
            if (logins.size > 3) {
                logins = logins.sortedBy { it.value }.takeLast(3).toMutableSet()
            }
            try {
                lock.tryLock()
                file.clear()
                logins.forEach { file.writeLine(it.value.toString()) }
            } finally {
                lock.unlock()
            }
        }
    }

    override fun save(data: List<Login>): Int {
        return 0
    }

    fun lastLogin(now: Login) = logins.sortedByDescending { it.value }.first { it != now }

    private fun File.clear() {
        writeText("")
    }

}