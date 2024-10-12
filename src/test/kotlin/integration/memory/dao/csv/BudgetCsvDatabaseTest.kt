package integration.memory.dao.csv

import dao.asAuditableEntity
import dao.csv.BudgetCsvDatabase
import domain.Budget
import domain.Category
import domain.Value
import helpers.fixtures.lastModifiedString
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSingleElement
import io.kotest.matchers.shouldBe
import java.io.File
import java.util.*
import kotlin.time.Duration

class BudgetCsvDatabaseTest : FunSpec({
    beforeEach {
        if (file.exists()) file.delete()
        file.createNewFile()
    }

    afterEach { if (file.exists()) file.delete() }

    test("can read from a file") {
        val uuid = UUID.randomUUID()

        file.writeText(
            """
            id,last_modified,category,value
            $uuid,$lastModifiedString,Food,200.00
            """.trimIndent()
        )

        database().selectAll() shouldHaveSingleElement Budget(
            Category("Food"),
            Value.of(200.0)
        ).asAuditableEntity(uuid)
    }

    test("can flush a standing order reminder to a file") {
        file.writeText("id,last_modified,category,value")
        val database = database()
        val id = database.save(
            Budget(
                Category("Food"),
                Value.of(200.0)
            )
        )

        database.flush()

        file.readText() shouldBe """
            id,last_modified,category,value
            $id,$lastModifiedString,Food,200.0
        """.trimIndent()
    }
})

private const val FILE_LOCATION = "test.csv"
private val file = File(FILE_LOCATION)

private fun database() = BudgetCsvDatabase(Duration.ZERO, FILE_LOCATION) { helpers.fixtures.lastModified }