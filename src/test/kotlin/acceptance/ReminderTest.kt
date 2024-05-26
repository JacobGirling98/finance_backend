package acceptance

import acceptance.setup.E2ETest
import dao.Entity
import domain.Date
import domain.Description
import domain.Frequency
import domain.FrequencyQuantity
import domain.Reminder
import helpers.fixtures.deserialize
import helpers.matchers.shouldContainDomain
import http.model.ReminderId
import io.kotest.matchers.collections.shouldHaveSize
import org.http4k.core.Status.Companion.NO_CONTENT
import org.http4k.core.Status.Companion.OK
import org.http4k.kotest.shouldHaveStatus
import java.time.LocalDate

class ReminderTest : E2ETest({

    test("can add a reminder, get it back, advance the reminder and then not get it back") {
        val before = LocalDate.now().minusWeeks(1)

        val addReminderResponse = client.post(
            "/reminders",
            """
            {
                "date": "$before",
                "frequency": "MONTHLY",
                "frequencyQuantity": 1,
                "description": "A reminder"
            }
            """.trimIndent()
        )

        addReminderResponse shouldHaveStatus OK
        val id = addReminderResponse.deserialize<ReminderId>()

        val outstandingRemindersResponse = client.get("/reminders")

        outstandingRemindersResponse shouldHaveStatus OK
        outstandingRemindersResponse.deserialize<List<Entity<Reminder>>>().let { reminders ->
            reminders shouldHaveSize 1
            reminders shouldContainDomain Reminder(
                Date(before),
                Frequency.MONTHLY,
                FrequencyQuantity(1),
                Description("A reminder")
            )
        }

        val advanceReminderResponse = client.post("/reminders/advance", """{"id": "${id.id}"}""")

        advanceReminderResponse shouldHaveStatus NO_CONTENT

        val emptyRemindersResponse = client.get("/reminders")

        emptyRemindersResponse shouldHaveStatus OK
        emptyRemindersResponse.deserialize<List<Entity<Reminder>>>() shouldHaveSize 0
    }
})
