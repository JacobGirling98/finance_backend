package integration.memory.dao.csv

import dao.asEntity
import dao.csv.ReminderCsvDatabase
import domain.Date
import domain.Description
import domain.Frequency
import domain.FrequencyQuantity
import domain.Reminder
import helpers.fixtures.lastModified
import helpers.fixtures.lastModifiedString
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSingleElement
import io.kotest.matchers.shouldBe
import java.io.File
import java.time.LocalDate
import java.util.*
import kotlin.time.Duration

class ReminderCsvDatabaseTest : FunSpec({
    beforeEach {
        if (file.exists()) file.delete()
        file.createNewFile()
    }

    afterEach { if (file.exists()) file.delete() }

    test("can read from a file") {
        val uuid = UUID.randomUUID()

        file.writeText(
            """
            id,last_modified,next_reminder,frequency_unit,frequency_quantity,description
            $uuid,$lastModifiedString,2020-01-01,monthly,1,Refresh Standing Orders
            """.trimIndent()
        )

        database().selectAll() shouldHaveSingleElement Reminder(
            Date(LocalDate.of(2020, 1, 1)),
            Frequency.MONTHLY,
            FrequencyQuantity(1),
            Description("Refresh Standing Orders")
        ).asEntity(uuid) { lastModified }
    }

    test("can flush a standing order reminder to a file") {
        file.writeText("id,last_modified,next_reminder,frequency_unit,frequency_quantity,description")
        val database = database()
        val id = database.save(
            Reminder(
                Date(LocalDate.of(2020, 1, 1)),
                Frequency.MONTHLY,
                FrequencyQuantity(1),
                Description("Refresh Standing Orders")
            )
        )

        database.flush()

        file.readText() shouldBe """
            id,last_modified,next_reminder,frequency_unit,frequency_quantity,description
            $id,$lastModifiedString,2020-01-01,monthly,1,Refresh Standing Orders
        """.trimIndent()
    }
})

private const val FILE_LOCATION = "test.csv"
private val file = File(FILE_LOCATION)

private fun database() = ReminderCsvDatabase(Duration.ZERO, FILE_LOCATION) { lastModified }
