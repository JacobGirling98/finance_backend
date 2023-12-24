package unit.resource

import dao.Database
import domain.Reminder
import io.kotest.core.spec.style.FunSpec
import io.mockk.mockk
import java.util.*

class ReminderProcessorTest : FunSpec({

    val database = mockk<Database<Reminder, UUID>>()
    val processor = ReminderProcessor(database)

    test("can move reminder to next date")
})

class ReminderProcessor(database: Database<Reminder, UUID>) {

}
