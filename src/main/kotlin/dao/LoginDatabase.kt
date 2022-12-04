package dao

import java.io.File
import java.time.LocalDate

data class Login(
    val value: LocalDate
)

class LoginDatabase(
    filePath: String
) : Database<Login> {

    private val file = File("$filePath/logins.txt")
    private var logins = mutableSetOf<Login>()

    fun initialise() {
        file.readLines().forEach { logins.add(Login(LocalDate.parse(it))) }
    }

    override fun save(data: Login) {
        if (!logins.contains(data)) {
            logins.add(data)
            if (logins.size > 3) {
                logins = logins.sortedBy { it.value }.takeLast(3).toMutableSet()
            }
            file.clear()
            logins.forEach { file.writeLine(it.value.toString()) }
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