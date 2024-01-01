package unit.http.handler

import config.CustomJackson.mapper
import dao.Entity
import dao.asEntity
import domain.*
import domain.Date
import helpers.fixtures.aReminder
import http.handler.addReminderHandler
import http.handler.advanceReminderHandler
import http.handler.outstandingRemindersHandler
import io.kotest.core.spec.style.FunSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status.Companion.NO_CONTENT
import org.http4k.core.Status.Companion.OK
import org.http4k.kotest.shouldHaveBody
import org.http4k.kotest.shouldHaveStatus
import java.time.LocalDate
import java.util.*

class ReminderHandlersTest : FunSpec({

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
        every { outstandingReminders() } returns listOf(aReminder().asEntity(id))

        val response = outstandingRemindersHandler(outstandingReminders)(request)

        response shouldHaveStatus OK
        response shouldHaveBody mapper.writeValueAsString(listOf(aReminder().asEntity(id)))
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

        response shouldHaveStatus NO_CONTENT
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

})
