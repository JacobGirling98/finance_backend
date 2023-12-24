package integration.dao.csv

import dao.asEntity
import dao.csv.ReminderCsvDatabase
import domain.*
import domain.Date
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
            id,next_reminder,frequency_unit,frequency_quantity,description
            $uuid,2020-01-01,monthly,1,Refresh Standing Orders
            """.trimIndent()
        )

        database().selectAll() shouldHaveSingleElement Reminder(
            Date(LocalDate.of(2020, 1, 1)),
            Frequency.MONTHLY,
            FrequencyQuantity(1),
            Description("Refresh Standing Orders")
        ).asEntity(uuid)
    }

    test("can flush a standing order reminder to a file") {
        file.writeText("id,next_reminder,frequency_unit,frequency_quantity,description")
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
            id,next_reminder,frequency_unit,frequency_quantity,description
            $id,2020-01-01,monthly,1,Refresh Standing Orders
        """.trimIndent()
    }
})

private const val FILE_LOCATION = "test.csv"
private val file = File(FILE_LOCATION)

private fun database() = ReminderCsvDatabase(Duration.ZERO, FILE_LOCATION)