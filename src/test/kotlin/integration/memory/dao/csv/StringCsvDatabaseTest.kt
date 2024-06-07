package integration.memory.dao.csv

import dao.AuditableEntity
import dao.csv.StringCsvDatabase
import helpers.fixtures.lastModified
import helpers.fixtures.lastModifiedString
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import java.io.File
import java.util.*
import kotlin.time.Duration

class StringCsvDatabaseTest : FunSpec({

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
            $id,$lastModifiedString,testing
            """.trimIndent()
        )

        database().selectAll() shouldContain AuditableEntity(id, "testing", lastModified)
    }

    test("can flush a single value to a file") {
        file.writeText("id,last_modified,string\n")
        val database = database()
        val id = database.save("testing")

        database.flush()

        file.readText() shouldBe """
            id,last_modified,value
            $id,$lastModifiedString,testing
        """.trimIndent()
    }
})

private const val FILE_LOCATION = "test.csv"
private val file = File(FILE_LOCATION)

private fun database() = StringCsvDatabase(Duration.ZERO, FILE_LOCATION) { lastModified }
