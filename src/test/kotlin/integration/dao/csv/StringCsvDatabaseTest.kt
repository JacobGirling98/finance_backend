package integration.dao.csv

import dao.csv.StringCsvDatabase
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import unit.matchers.shouldContainDomain
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
            id,value
            $id,testing
            """.trimIndent()
        )

        database().selectAll() shouldContainDomain "testing"
    }

    test("can flush a single value to a file") {
        file.writeText("id,string\n")
        val database = database()
        val id = database.save("testing")

        database.flush()

        file.readText() shouldBe """
            id,value
            $id,testing
        """.trimIndent()
    }
})

private const val FILE_LOCATION = "test.csv"
private val file = File(FILE_LOCATION)

private fun database() = StringCsvDatabase(Duration.ZERO, FILE_LOCATION)
