package unit.resource

import dao.Database
import dao.Entity
import dao.asEntity
import domain.*
import domain.Date
import io.kotest.core.spec.style.FunSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.LocalDate
import java.util.*

class ReminderProcessorTest : FunSpec({

    val id = UUID.randomUUID()
    val database = mockk<Database<Reminder, UUID>>()
    val processor = ReminderProcessor(database)

    test("can move reminder to next date") {
        val reminder = Reminder(
            Date(LocalDate.of(2023, 1, 1)),
            Frequency.MONTHLY,
            FrequencyQuantity(1),
            Description("A reminder")
        ).asEntity(id)
        every { database.update(any()) } returns null

        processor.markAsRead(reminder)

        verify {
            database.update(
                Reminder(
                    Date(LocalDate.of(2023, 2, 1)),
                    Frequency.MONTHLY,
                    FrequencyQuantity(1),
                    Description("A reminder")
                ).asEntity(id)
            )
        }
    }
})

class ReminderProcessor(database: Database<Reminder, UUID>) {
    fun markAsRead(reminder: Entity<Reminder>) {

    }

}


