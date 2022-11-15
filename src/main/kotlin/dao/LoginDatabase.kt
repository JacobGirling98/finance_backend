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
    private val logins = ArrayDeque<Login>()

    fun initialise() {
        file.readLines().forEach { logins.add(Login(LocalDate.parse(it))) }
    }

    override fun save(data: Login) {
        if (data !in logins) {
            logins.add(data)
            if (logins.size > 3) {
                logins.removeFirst()
            }
            file.clear()
            logins.forEach { file.writeLine(it.value.toString()) }
        }
    }

    override fun save(data: List<Login>) {}

    fun lastLogin() = logins.last()

    private fun File.clear() {
        writeText("")
    }

}