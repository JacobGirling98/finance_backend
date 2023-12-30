package unit.resource

import dao.Database
import dao.asEntity
import domain.*
import domain.Date
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import resource.ReminderProcessor
import unit.fixtures.aReminder
import unit.fixtures.withADateOf
import java.time.LocalDate
import java.util.*

class ReminderProcessorTest : FunSpec({

    val id = UUID.randomUUID()
    val now = { LocalDate.of(2023, 6, 1) }

    val database = mockk<Database<Reminder, UUID>>()
    val processor = ReminderProcessor(database, now)

    test("can move reminder to next date") {
        val reminder = Reminder(
            Date(LocalDate.of(2023, 1, 1)),
            Frequency.MONTHLY,
            FrequencyQuantity(1),
            Description("A reminder")
        ).asEntity(id)
        every { database.findById(id) } returns reminder
        every { database.update(any()) } returns null

        processor.markAsRead(id)

        verify { database.findById(id) }
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

    test("can get all reminders that are due") {
        val firstReminder = aReminder().withADateOf(now().minusMonths(1))
        val secondReminder = aReminder().withADateOf(now().minusMonths(2))
        val notDueReminder = aReminder().withADateOf(now().plusMonths(1))
        val anotherId = UUID.randomUUID()
        val notDueId = UUID.randomUUID()

        every { database.selectAll() } returns listOf(
            firstReminder.asEntity(id),
            secondReminder.asEntity(anotherId),
            notDueReminder.asEntity(notDueId)
        )

        processor.allRemindersDue().map { it.id } shouldContainExactlyInAnyOrder listOf(id, anotherId)
    }
})


