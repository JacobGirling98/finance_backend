package integration.memory.dao.csv

import dao.csv.CsvDatabase
import helpers.fixtures.Doubles.TestDomain
import helpers.matchers.shouldContainDomain
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.io.File
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
            id,name,age
            $id,Jacob,24
            """.trimIndent()
        )

        database().selectAll() shouldContainDomain TestDomain("Jacob", 24)
    }

    test("can flush changes to a file") {
        file.writeText("id,name,age")

        val database = database()
        val newId = database.save(TestDomain("Jacob", 24))

        database.flush()

        file.readText() shouldBe """
            id,name,age
            $newId,Jacob,24
        """.trimIndent()
    }

    test("can overwrite file") {
        file.writeText("id,name,age")
        val overwrittenContents = """
            id,name,age
            ${UUID.randomUUID()},Jacob,24
        """.trimIndent()

        val database = database()

        database.overwrite(overwrittenContents)

        file.readText() shouldBe overwrittenContents
    }
})

private const val FILE_LOCATION = "test.csv"
private val file = File(FILE_LOCATION)
private val id = UUID.randomUUID()

private fun database() = TestCsvDatabase(Duration.ZERO, FILE_LOCATION)

private class TestCsvDatabase(duration: Duration, file: String) : CsvDatabase<TestDomain>(duration, file) {
    override fun headers(): String = "name,age"

    override fun domainFromCommaSeparatedList(row: List<String>): TestDomain =
        TestDomain(row[indexOfColumn("name")], row[indexOfColumn("age")].toInt())

    override fun TestDomain.toRow(): String = "$name,$age"
}
