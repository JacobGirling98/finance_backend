package integration.memory.dao.csv

import dao.AuditableEntity
import dao.Entity
import dao.csv.CsvDatabase
import helpers.fixtures.Doubles.TestDomain
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSingleElement
import io.kotest.matchers.shouldBe
import java.io.File
import java.time.LocalDateTime
import java.util.*
import kotlin.time.Duration

class CsvDatabaseTest : FunSpec({

    beforeEach {
        if (file.exists()) file.delete()
        file.createNewFile()
    }

    afterEach { if (file.exists()) file.delete() }

    test("data is read on construction") {
        file.writeText(
            """
            id,last_modified,name,age
            $id,2024-01-01T00:00:00,Jacob,24
            """.trimIndent()
        )

        database().selectAll() shouldHaveSingleElement AuditableEntity(id, TestDomain("Jacob", 24), lastModified)
    }

    test("can flush changes to a file") {
        file.writeText("id,last_modified_name,age")

        val database = database()
        val newId = database.save(TestDomain("Jacob", 24))

        database.flush()

        file.readText() shouldBe """
            id,last_modified,name,age
            $newId,2024-01-01T00:00:00,Jacob,24
        """.trimIndent()
    }

    test("can overwrite file") {
        file.writeText("id,last_modified,name,age")
        val overwrittenContents = """
            id,last_modified,name,age
            ${UUID.randomUUID()},2024-01-01T00:00:00,Jacob,24
        """.trimIndent()

        val database = database()

        database.overwrite(overwrittenContents)

        file.readText() shouldBe overwrittenContents
    }

    test("database can instantiate against empty file") {
        val database = database()

        database.selectAll().shouldBeEmpty()
    }
})

private const val FILE_LOCATION = "test.csv"
private val file = File(FILE_LOCATION)
private val id = UUID.randomUUID()
private val lastModified = LocalDateTime.of(2024, 1, 1, 0, 0)

private fun database() = TestCsvDatabase(Duration.ZERO, FILE_LOCATION)

private class TestCsvDatabase(
    duration: Duration,
    file: String
) : CsvDatabase<TestDomain>(duration, file, now = { lastModified }) {
    override fun headers(): String = "name,age"

    override fun domainFromCommaSeparatedList(row: List<String>): TestDomain =
        TestDomain(row[indexOfColumn("name")], row[indexOfColumn("age")].toInt())

    override fun TestDomain.toRow(): String = "$name,$age"
}
