package acceptance

import acceptance.setup.E2ETest
import dao.Entity
import domain.*
import helpers.fixtures.deserialize
import helpers.matchers.shouldContainDomain
import io.kotest.matchers.collections.shouldHaveSize
import org.http4k.core.Status.Companion.NO_CONTENT
import org.http4k.core.Status.Companion.OK
import org.http4k.kotest.shouldHaveStatus
import java.time.LocalDate

class ReminderTest : E2ETest({

    test("can add a reminder and then get them back") {
        val addReminderResponse = client.post(
            "/reminders",
            """
            {
                "date": "2023-01-01",
                "frequency": "MONTHLY",
                "frequencyQuantity": 1,
                "description": "A reminder"
            }
            """.trimIndent()
        )

        addReminderResponse shouldHaveStatus NO_CONTENT

        val outstandingRemindersResponse = client.get("/reminders")

        outstandingRemindersResponse shouldHaveStatus OK
        outstandingRemindersResponse.deserialize<List<Entity<Reminder>>>().let { reminders ->
            reminders shouldHaveSize 1
            reminders shouldContainDomain Reminder(
                Date(LocalDate.of(2023, 1, 1)),
                Frequency.MONTHLY,
                FrequencyQuantity(1),
                Description("A reminder")
            )
        }
    }

})