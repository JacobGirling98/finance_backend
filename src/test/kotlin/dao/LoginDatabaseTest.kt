package dao

import com.natpryce.hamkrest.and
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.hasElement
import com.natpryce.hamkrest.hasSize
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File
import java.time.LocalDate

class LoginDatabaseTest {

    private val tmp: String = "tmp"
    private val filePath = "logins.txt"
    private val logins = LoginDatabase(tmp)

    @BeforeEach
    fun setup() {
        File(tmp).deleteRecursively()
        File(tmp).mkdirs()
        file().createNewFile()
    }

    @AfterEach
    fun teardown() {
        File(tmp).deleteRecursively()
    }

    @Test
    fun `save a login in an empty file`() {
        val login = LocalDate.of(2021, 1, 1)
        logins.initialise()

        logins.save(Login(login))

        assertThat(file().readLines(), hasSize(equalTo(1)) and hasElement(login.toString()))
    }

    @Test
    fun `save a login in a file with an existing login`() {
        val existing = LocalDate.of(2020, 1, 1)
        val login = LocalDate.of(2021, 1, 1)
        file().writeLine(existing.toString())
        logins.initialise()

        logins.save(Login(login))

        assertThat(
            file().readLines(), hasSize(equalTo(2))
                    and hasElement(existing.toString())
                    and hasElement(login.toString())
        )
    }

    @Test
    fun `saving a login to a file with the same date does not save a duplicate`() {
        val existing = LocalDate.of(2020, 1, 1)
        file().writeLine(existing.toString())
        logins.initialise()

        logins.save(Login(existing))

        assertThat(file().readLines(), hasSize(equalTo(1)))
    }

    @Test
    fun `saving a login to a file with 3 logins removes the oldest`() {
        val firstDate = LocalDate.of(2019, 1, 1)
        val secondDate = LocalDate.of(2020, 1, 1)
        val thirdDate = LocalDate.of(2021, 1, 1)
        val fourthDate = LocalDate.of(2022, 1, 1)
        listOf(
            firstDate,
            secondDate,
            thirdDate
        ).forEach { file().writeLine(it.toString()) }
        logins.initialise()

        logins.save(Login(fourthDate))

        assertThat(
            file().readLines(), hasSize(equalTo(3))
                    and hasElement(secondDate.toString())
                    and hasElement(thirdDate.toString())
                    and hasElement(fourthDate.toString())
                    and !hasElement(firstDate.toString())
        )
    }

    private fun file() = File("$tmp/$filePath")
}