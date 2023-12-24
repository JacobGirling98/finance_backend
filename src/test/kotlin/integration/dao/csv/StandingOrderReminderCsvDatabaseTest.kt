package integration.dao.csv

import dao.asEntity
import dao.csv.StandingOrderReminderCsvDatabase
import domain.Frequency
import domain.FrequencyQuantity
import domain.NextReminder
import domain.StandingOrderReminder
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSingleElement
import io.kotest.matchers.shouldBe
import java.io.File
import java.time.LocalDate
import java.util.*
import kotlin.time.Duration

class StandingOrderReminderCsvDatabaseTest : FunSpec({
    beforeEach {
        if (file.exists()) file.delete()
        file.createNewFile()
    }

    afterEach { if (file.exists()) file.delete() }

    test("can read from a file") {
        val uuid = UUID.randomUUID()

        file.writeText(
            """
            id,next_reminder,frequency_unit,frequency_quantity
            $uuid,2020-01-01,monthly,1
            """.trimIndent()
        )

        database().selectAll() shouldHaveSingleElement StandingOrderReminder(
            NextReminder(LocalDate.of(2020, 1, 1)),
            Frequency.MONTHLY,
            FrequencyQuantity(1)
        ).asEntity(uuid)
    }

    test("can flush a standing order reminder to a file") {
        file.writeText("id,next_reminder,frequency_unit,frequency_quantity")
        val database = database()
        val id = database.save(
            StandingOrderReminder(
                NextReminder(LocalDate.of(2020, 1, 1)),
                Frequency.MONTHLY,
                FrequencyQuantity(1)
            )
        )

        database.flush()

        file.readText() shouldBe """
            id,next_reminder,frequency_unit,frequency_quantity
            $id,2020-01-01,monthly,1
        """.trimIndent()
    }
})

private const val FILE_LOCATION = "test.csv"
private val file = File(FILE_LOCATION)

private fun database() = StandingOrderReminderCsvDatabase(Duration.ZERO, FILE_LOCATION)