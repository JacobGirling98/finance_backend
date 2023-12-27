package integration.memory.dao.csv

import dao.csv.DateCsvDatabase
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import unit.matchers.shouldContainDomain
import java.io.File
import java.time.LocalDate
import java.util.*
import kotlin.time.Duration

class DateCsvDatabaseTest : FunSpec({

    beforeEach {
        if (file.exists()) file.delete()
        file.createNewFile()
    }

    afterEach { if (file.exists()) file.delete() }

    test("can read from a file") {
        val id = UUID.randomUUID()

        file.writeText(
            """
            id,value
            $id,2020-01-01
            """.trimIndent()
        )

        database().selectAll() shouldContainDomain LocalDate.of(2020, 1, 1)
    }

    test("can flush a single value to a file") {
        file.writeText("id,string\n")
        val database = database()
        val id = database.save(LocalDate.of(2020, 1, 1))

        database.flush()

        file.readText() shouldBe """
            id,value
            $id,2020-01-01
        """.trimIndent()
    }
})

private const val FILE_LOCATION = "test.csv"
private val file = File(FILE_LOCATION)

private fun database() = DateCsvDatabase(Duration.ZERO, FILE_LOCATION)
