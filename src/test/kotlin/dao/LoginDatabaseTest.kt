package dao

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSingleElement
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotContain
import java.io.File
import java.time.LocalDate

class LoginDatabaseTest : FunSpec({

    val logins = LoginDatabase(tmp)

    beforeEach {
        File(tmp).deleteRecursively()
        File(tmp).mkdirs()
        file().createNewFile()
    }

    afterEach {
        File(tmp).deleteRecursively()
    }

    test("save a login in an empty file") {
        val login = LocalDate.of(2021, 1, 1)
        logins.initialise()

        logins.save(Login(login))

        file().readLines().shouldHaveSingleElement(login.toString())
    }

    test("save a login in a file with an existing login") {
        val existing = LocalDate.of(2020, 1, 1)
        val login = LocalDate.of(2021, 1, 1)
        file().writeLine(existing.toString())
        logins.initialise()

        logins.save(Login(login))

        file().readLines()
            .shouldHaveSize(2)
            .shouldContain(existing.toString())
            .shouldContain(login.toString())
    }

    test("saving a login to a file with the same date does not save a duplicate") {
        val existing = LocalDate.of(2020, 1, 1)
        file().writeLine(existing.toString())
        logins.initialise()

        logins.save(Login(existing))

        file().readLines().shouldHaveSize(1)
    }

    test("saving a login to a file with 3 logins removes the oldest") {
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

        file().readLines()
            .shouldHaveSize(3)
            .shouldContain(secondDate.toString())
            .shouldContain(thirdDate.toString())
            .shouldContain(fourthDate.toString())
            .shouldNotContain(firstDate.toString())
    }
})

private const val tmp: String = "tmp"
private const val filePath = "logins.txt"
private fun file() = File("$tmp/$filePath")