package integration.memory.dao.csv

import dao.AuditableEntity
import dao.Entity
import dao.csv.DateCsvDatabase
import helpers.fixtures.lastModified
import helpers.fixtures.lastModifiedString
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
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
            id,last_modified,value
            $id,$lastModifiedString,2020-01-01
            """.trimIndent()
        )

        database().selectAll() shouldContain AuditableEntity(id, LocalDate.of(2020, 1, 1), lastModified)
    }

    test("can flush a single value to a file") {
        file.writeText("id,last_modified,string\n")
        val database = database()
        val id = database.save(LocalDate.of(2020, 1, 1))

        database.flush()

        file.readText() shouldBe """
            id,last_modified,value
            $id,$lastModifiedString,2020-01-01
        """.trimIndent()
    }
})

private const val FILE_LOCATION = "test.csv"
private val file = File(FILE_LOCATION)

private fun database() = DateCsvDatabase(Duration.ZERO, FILE_LOCATION) { lastModified }
