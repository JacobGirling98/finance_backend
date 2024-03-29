package unit.http.handler

import config.CustomJackson.mapper
import dao.Entity
import dao.asEntity
import domain.Date
import domain.Description
import domain.Frequency
import domain.FrequencyQuantity
import domain.Reminder
import helpers.fixtures.aReminder
import helpers.fixtures.deserialize
import http.handler.addReminderHandler
import http.handler.advanceReminderHandler
import http.handler.outstandingRemindersHandler
import http.handler.updateReminderHandler
import http.model.ReminderId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status.Companion.NO_CONTENT
import org.http4k.core.Status.Companion.OK
import org.http4k.kotest.shouldHaveBody
import org.http4k.kotest.shouldHaveStatus
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

class ReminderHandlersTest : FunSpec({

    val now = { LocalDateTime.of(2024, 1, 1, 0, 0) }

    test("can advance a reminder") {
        val id = UUID.randomUUID()
        val advanceReminder = mockk<(UUID) -> Unit>(relaxed = true)
        val request = Request(Method.POST, "/").body(
            """
            { "id": "$id" }
            """.trimIndent()
        )

        val response = advanceReminderHandler(advanceReminder)(request)

        response shouldHaveStatus NO_CONTENT
        verify { advanceReminder(id) }
    }

    test("can get outstanding reminders") {
        val id = UUID.randomUUID()
        val outstandingReminders = mockk<() -> List<Entity<Reminder>>>()
        val request = Request(Method.GET, "/")
        every { outstandingReminders() } returns listOf(aReminder().asEntity(id, now))

        val response = outstandingRemindersHandler(outstandingReminders)(request)

        response shouldHaveStatus OK
        response shouldHaveBody mapper.writeValueAsString(listOf(aReminder().asEntity(id, now)))
    }

    test("can add a reminder") {
        val addReminder = mockk<(Reminder) -> UUID>()
        val id = UUID.randomUUID()
        val request = Request(Method.POST, "/").body(
            """
            {
                "date": "2023-01-01",
                "frequency": "MONTHLY",
                "frequencyQuantity": 1,
                "description": "Remind me"
            }    
            """.trimIndent()
        )
        every { addReminder(any()) } returns id

        val response = addReminderHandler(addReminder)(request)

        response shouldHaveStatus OK
        response.deserialize<ReminderId>().id shouldBe id
        verify {
            addReminder(
                Reminder(
                    Date(LocalDate.of(2023, 1, 1)),
                    Frequency.MONTHLY,
                    FrequencyQuantity(1),
                    Description("Remind me")
                )
            )
        }
    }

    test("can update a reminder") {
        val updateReminder = mockk<(Entity<Reminder>) -> Unit>()
        val id = UUID.randomUUID()
        val request = Request(Method.PUT, "/").body(
            """
            {
                "id": "$id",
                "domain": {
                    "date": "2023-01-01",
                    "frequency": "MONTHLY",
                    "frequencyQuantity": 1,
                    "description": "Remind me"
                },
                "lastModified": "2024-01-01T00:00:00"
            }    
            """.trimIndent()
        )
        every { updateReminder(any()) } just runs

        val response = updateReminderHandler(updateReminder)(request)

        response shouldHaveStatus NO_CONTENT
        verify {
            updateReminder(
                Reminder(
                    Date(LocalDate.of(2023, 1, 1)),
                    Frequency.MONTHLY,
                    FrequencyQuantity(1),
                    Description("Remind me")
                ).asEntity(id, now)
            )
        }
    }
})
